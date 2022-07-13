/*
 * Copyright (c) 2022 nosqlbench
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nosqlbench.converters.cql.cqlast;

import io.nosqlbench.nb.api.labels.Labeled;

import java.util.Map;

public class CqlColumnDef implements Labeled {
    private String table;
    private String keyspace;
    private final String name;
    private final String type;

    public CqlColumnDef(String colname, String typedef) {
        this.type = typedef;
        this.name = colname;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getTable() {
        return table;
    }

    public String getKeyspace() {
        return keyspace;
    }

    @Override
    public String toString() {
        return getLabels().toString();
    }

    @Override
    public Map<String, String> getLabels() {
        return Map.of(
            "column", name,
            "typedef", type,
            "table", table,
            "keyspace", keyspace
        );
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public void setTable(String table) {
        this.table = table;
    }
}