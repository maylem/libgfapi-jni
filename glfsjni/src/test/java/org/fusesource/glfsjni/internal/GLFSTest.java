/*
 * Copyright (C) 2011, FuseSource Corp.  All rights reserved.
 *
 *     http://fusesource.com
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *    * Neither the name of FuseSource Corp. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.fusesource.glfsjni.internal;

import org.testng.annotations.Test;

import static org.fusesource.glfsjni.internal.GLFS.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * A Unit test for the GLFS class implementation.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a> & <a href="http://about.me/louiszuckerman">Louis Zuckerman</a>
 */
public class GLFSTest {

    public static final String PATH = "bar";
    public static final String HELLO_WORLD = "hello world";
    private long vol;
    private long file;

    @Test
    public void testNew() {
        vol = glfs_new("foo");
        System.out.println("NEW: " + vol);
        assertTrue(0 < vol);
    }

    @Test(dependsOnMethods = "testNew")
    public void testSetlog() {
        int setlog = glfs_set_logging(vol, "glfsjni/target/glfsjni.log", 7);
        System.out.println("SETLOG: " + setlog);
        assertEquals(0, setlog);
    }

    @Test(dependsOnMethods = "testSetlog")
    public void testServer() {
        int server = glfs_set_volfile_server(vol, "tcp", "127.0.2.1", 24007);
        System.out.println("SERVER: " + server);
        assertEquals(0, server);
    }

    @Test(dependsOnMethods = "testServer")
    public void testInit() {
        int init = glfs_init(vol);
        System.out.println("INIT: " + init);
        assertEquals(0, init);
    }

    @Test(dependsOnMethods = "testInit")
    public void testOpen_nonExisting() {
        file = glfs_open(vol, PATH, 0);
        System.out.println("OPEN: " + file);
        assertEquals(0, file);
    }

    @Test(dependsOnMethods = "testOpen_nonExisting")
    public void testCreate() {
        file = glfs_creat(vol, PATH, 0, 0666);
        System.out.println("CREAT: " + file);
        assertTrue(file > 0);
    }

    @Test(dependsOnMethods = "testCreate")
    public void testWrite() {
        int length = HELLO_WORLD.length();
        int write = glfs_write(file, HELLO_WORLD.getBytes(), length, 0);

        System.out.println("WRITE: " + write);

        assertEquals(length, write);
    }

    @Test(dependsOnMethods = "testWrite")
    public void testSeek() {
        int seek = glfs_lseek(file, 0, 0);
        System.out.println("SEEK: " + seek);
        assertEquals(0, seek);
    }

    @Test(dependsOnMethods = "testSeek")
    public void testRead() {
        int length = HELLO_WORLD.length();
        byte[] content = new byte[length];
        long read = glfs_read(file, content, length, 0);

        String readValue = new String(content);
        System.out.println("READ val: " + readValue);
        System.out.println("READ len: " + read);

        assertEquals(length, read);
        assertEquals(HELLO_WORLD, readValue);
    }

    @Test(dependsOnMethods = "testRead")
    public void testFromGlfd() {
        long glfs = glfs_from_glfd(file);
        System.out.println("GLFS_GLFD: " + glfs);
        assertEquals(vol, glfs);
    }

    @Test(dependsOnMethods = "testFromGlfd")
    public void testClose() {
        int close = glfs_close(file);
        System.out.println("CLOSE: " + close);
        assertEquals(0, close);
    }

    @Test(dependsOnMethods = "testClose")
    public void testUnlink() {
        int unl = glfs_unlink(vol, PATH);
        System.out.println("UNLINK: " + unl);
    }

    @Test(dependsOnMethods = "testUnlink")
    public void testFini() {
        int fini = glfs_fini(vol);
        System.out.println("FINI: " + fini);
        assertEquals(-1, fini);
    }

}
