/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package is.hax.spring.web.multipart;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartException;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;


/**
 * @author Vidar Svansson
 * @since: 05.26.2009
 */
public class StreamingMultipartFile implements MultipartFile {


    private FileItemStream item;
    private long size = -1;
    byte[] bytes;

    public StreamingMultipartFile(FileItemStream item) throws IOException {

        this.item = item;
        getBytes();

    }


    public String getName() {
        return item.getName();
    }

    public String getOriginalFilename() {
        return item.getFieldName();
    }

    public String getContentType() {
        return item.getContentType();
    }

    public boolean isEmpty() {
        return false; // TODO
    }

    public long getSize() {
        if (size > 0) {
            try {
                return getBytes().length;
            } catch (IOException e) {

                throw new MultipartException("Something went wrong here");
            }

        }
        return size;
    }

    public byte[] getBytes() throws IOException {
        if(bytes == null) {
            bytes =  IOUtils.toByteArray(item.openStream());
        }

        return bytes;


    }

    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("not possible");

    }

    public void transferTo(File dest) throws IOException, IllegalStateException {
        throw new UnsupportedOperationException("not possible");
    }
}
