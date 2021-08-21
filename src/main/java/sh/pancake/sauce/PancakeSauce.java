/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import sh.pancake.sauce.asm.PancakeClassRemapper;
import sh.pancake.sauce.parser.ConversionTable;

/**
 * Main remapper class
 */
public class PancakeSauce {

    private ZipInputStream stream;
    private ConversionTable table;

    private Function<ZipEntry, Boolean> filter;

    public PancakeSauce(ZipInputStream stream, ConversionTable table) {
        this(stream, table, null);
    }

    public PancakeSauce(ZipInputStream stream, ConversionTable table, Function<ZipEntry, Boolean> filter) {
        this.stream = stream;
        this.table = table;
        this.filter = filter;
    }

    /**
     * Remap class files in archive stream and emit to output
     *
     * @param output Output stream
     * @throws IOException
     */
    public void remapJar(ZipOutputStream output) throws IOException {
        remapJar(output, null);
    }

    /**
     * Remap class files in archive stream and emit to output
     *
     * @param output     Output stream
     * @param progressCb Progress callback
     * @throws IOException
     */
    public void remapJar(ZipOutputStream output, Consumer<RemapInfo> progressCb) throws IOException {
        Set<String> entries = new HashSet<>();

        for (ZipEntry entry = stream.getNextEntry(); entry != null; entry = stream.getNextEntry()) {
            if (entries.contains(entry.getName())) continue;

            if (entry.getName().endsWith(".class") && (filter == null || filter.apply(entry))) {
                RemapInfo info = rewriteEntry(stream.readAllBytes(), entry, output);

                if (progressCb != null) progressCb.accept(info);
            } else {
                output.putNextEntry(entry);
                stream.transferTo(output);
                output.closeEntry();
            }

            entries.add(entry.getName());

            stream.closeEntry();
        }
    }

    /**
     * Remap class files in archive stream and emit to output asynchronously
     *
     * @param service    Executor
     * @param output     Output stream
     * @throws CompletionException
     */
    public CompletableFuture<Void> remapJarAsync(Executor executor, ZipOutputStream output) throws IOException, InterruptedException {
        return remapJarAsync(executor, output, null);
    }

    /**
     * Remap class files in archive stream and emit to output asynchronously
     *
     * @param service    Executor
     * @param output     Output stream
     * @param progressCb Progress callback
     * @throws CompletionException
     */
    public CompletableFuture<Void> remapJarAsync(Executor executor, ZipOutputStream output, Consumer<RemapInfo> progressCb) throws CompletionException {
        CompletableFuture<Void> future = new CompletableFuture<>();

        executor.execute(() -> {
            try {
                Set<String> entries = new HashSet<>();
                List<CompletableFuture<Void>> tasks = new ArrayList<>();

                for (ZipEntry entry = stream.getNextEntry(); entry != null; entry = stream.getNextEntry()) {
                    if (entries.contains(entry.getName()))
                        continue;

                    if (entry.getName().endsWith(".class") && (filter == null || filter.apply(entry))) {
                        ZipEntry currentEntry = entry;
                        byte[] data = stream.readAllBytes();

                        tasks.add(CompletableFuture.runAsync(() -> {
                            try {
                                RemapInfo info = rewriteEntry(data, currentEntry, output);

                                if (progressCb != null) progressCb.accept(info);
                            } catch (Exception e) {
                                throw new CompletionException(e);
                            }
                        }, executor));
                    } else {
                        synchronized (output) {
                            output.putNextEntry(entry);
                            stream.transferTo(output);
                            output.closeEntry();
                        }
                    }

                    entries.add(entry.getName());

                    stream.closeEntry();
                }

                for (CompletableFuture<Void> taskFuture : tasks) {
                    taskFuture.join();
                }

                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    private RemapInfo rewriteEntry(byte[] clazz, ZipEntry entry, ZipOutputStream output) throws IOException {
        RemapInfo info = this.remapClass(clazz);

        synchronized (output) {
            output.putNextEntry(new ZipEntry(info.getToName()));
            output.write(info.getData());
            output.closeEntry();
        }

        return info;
    }

    private RemapInfo remapClass(byte[] clazz) {
        ClassReader reader = new ClassReader(clazz);
        ClassWriter writer = new ClassWriter(0);
        PancakeClassRemapper remapper = new PancakeClassRemapper(writer, table);
        // ClassVisitor visitor = new DeobfClassVisitor(writer, Opcodes.ASM7, table);

        reader.accept(remapper, 0);

        byte[] written = writer.toByteArray();

        ClassReader newReader = new ClassReader(written);

        return new RemapInfo(written, reader.getClassName() + ".class", newReader.getClassName() + ".class");
    }

}
