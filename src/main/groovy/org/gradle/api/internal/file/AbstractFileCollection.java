/*
 * Copyright 2009 the original author or authors.
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
package org.gradle.api.internal.file;

import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.StopActionException;
import org.gradle.util.GUtil;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractFileCollection implements FileCollection {
    /**
     * Returns the display name of this file collection. Used in log and error messages.
     *
     * @return the display name
     */
    public abstract String getDisplayName();

    public File getSingleFile() throws IllegalStateException {
        Collection<File> files = getFiles();
        if (files.isEmpty()) {
            throw new IllegalStateException(String.format(
                    "Expected %s to contain exactly one file, however, it contains no files.", getDisplayName()));
        }
        if (files.size() != 1) {
            throw new IllegalStateException(String.format(
                    "Expected %s to contain exactly one file, however, it contains %d files.", getDisplayName(),
                    files.size()));
        }
        return files.iterator().next();
    }

    public Iterator<File> iterator() {
        return getFiles().iterator();
    }

    public String getAsPath() {
        return GUtil.join(getFiles(), File.pathSeparator);
    }

    public FileCollection plus(FileCollection collection) {
        return new UnionFileCollection(this, collection);
    }

    public Object addToAntBuilder(Object node, String childNodeName) {
        new AntFileCollectionBuilder(this).addToAntBuilder(node, childNodeName);
        return this;
    }

    public FileCollection stopActionIfEmpty() throws StopActionException {
        if (getFiles().isEmpty()) {
            throw new StopActionException(String.format("%s does not contain any files.", StringUtils.capitalize(getDisplayName())));
        }
        return this;
    }
}
