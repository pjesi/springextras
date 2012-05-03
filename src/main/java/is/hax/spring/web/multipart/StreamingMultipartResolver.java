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

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import is.hax.spring.web.multipart.StreamingMultipartFile;


/**
 * @author Vidar Svansson
 * @since: 05.26.2009
 */
public class StreamingMultipartResolver implements MultipartResolver {

    private long maxUploadSize = 50000L;

    public long getMaxUploadSize() {
        return maxUploadSize;
    }

    public void setMaxUploadSize(long maxUploadSize) {
        this.maxUploadSize = maxUploadSize;
    }

    public boolean isMultipart(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload();
        upload.setFileSizeMax(maxUploadSize);

        String encoding = determineEncoding(request);

        //Map<String, MultipartFile> multipartFiles = new HashMap<String, MultipartFile>();
        Map<String, String[]> multipartParameters = new HashMap<String, String[]>();
        Map<String, String> multipartContentTypes = new HashMap<String, String>();

        
        MultiValueMap<String, MultipartFile> multipartFiles = new LinkedMultiValueMap<String, MultipartFile>();



        // Parse the request
        try {
            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();


                String name = item.getFieldName();
                InputStream stream = item.openStream();
                if (item.isFormField()) {

                    String value = Streams.asString(stream, encoding);

                    String[] curParam = (String[]) multipartParameters.get(name);
                    if (curParam == null) {
                        // simple form field
                        multipartParameters.put(name, new String[]{value});
                    } else {
                        // array of simple form fields
                        String[] newParam = StringUtils.addStringToArray(curParam, value);
                        multipartParameters.put(name, newParam);
                    }


                } else {

                    // Process the input stream
                    MultipartFile file = new StreamingMultipartFile(item);
                    multipartFiles.add(name, file);
                    multipartContentTypes.put(name, file.getContentType());
                }
            }
        } catch (IOException e) {
            throw new MultipartException("something went wrong here",e);
        } catch (FileUploadException e) {
            throw new MultipartException("something went wrong here",e);
        }


        return new DefaultMultipartHttpServletRequest(request, multipartFiles, multipartParameters, multipartContentTypes);
    }

    public void cleanupMultipart(MultipartHttpServletRequest request) {

        // noop
    }


    /**
     * Determine the encoding for the given request.
     * Can be overridden in subclasses.
     * <p>The default implementation checks the request encoding,
     * falling back to the default encoding specified for this resolver.
     *
     * @param request current HTTP request
     * @return the encoding for the request (never <code>null</code>)
     * @see javax.servlet.ServletRequest#getCharacterEncoding
     */
    protected String determineEncoding(HttpServletRequest request) {
        String encoding = request.getCharacterEncoding();
        if (encoding == null) {
            encoding = "UTF-8";
        }
        return encoding;
    }

}
