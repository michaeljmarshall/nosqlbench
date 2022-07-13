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

import io.nosqlbench.converters.cql.generated.CqlParser;
import io.nosqlbench.converters.cql.generated.CqlParserBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public class CqlModelBuilder extends CqlParserBaseListener {

    private final CQBErrorListener errorListener;
    private final CqlModel model;

    public CqlModelBuilder(CQBErrorListener errorListener) {
        this.errorListener = errorListener;
        this.model = new CqlModel(errorListener);
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        System.out.println("error parsing: " + node.toString());
        ParseTree parent = node.getParent();
        String errorNodeType = parent.getClass().getSimpleName();
        System.out.println("error type: " + errorNodeType);
        System.out.println("source interval: " + node.getSourceInterval());

        super.visitErrorNode(node);
    }

    @Override
    public void enterCreateKeyspace(CqlParser.CreateKeyspaceContext ctx) {
        model.newKeyspace();
    }

    @Override
    public void exitCreateKeyspace(CqlParser.CreateKeyspaceContext ctx) {
        model.saveKeyspace(
            ctx.keyspace().getText(),
            textOf(ctx)
        );
    }

    @Override
    public void enterCreateTable(CqlParser.CreateTableContext ctx) {
        model.newTable();
    }

    @Override
    public void exitPrimaryKeyColumn(CqlParser.PrimaryKeyColumnContext ctx) {
        super.exitPrimaryKeyColumn(ctx);
    }

    @Override
    public void exitPrimaryKeyDefinition(CqlParser.PrimaryKeyDefinitionContext ctx) {
        if (ctx.singlePrimaryKey()!=null) {
            model.addPartitionKey(ctx.singlePrimaryKey().column().getText());
        } else if (ctx.compositeKey()!=null) {
            for (CqlParser.PartitionKeyContext pkctx : ctx.compositeKey().partitionKeyList().partitionKey()) {
                model.addPartitionKey(pkctx.column().getText());
            }
            for (CqlParser.ClusteringKeyContext ccol : ctx.compositeKey().clusteringKeyList().clusteringKey()) {
                model.addClusteringColumn(ccol.column().getText());
            }
        } else if (ctx.compoundKey()!=null) {
            model.addClusteringColumn(ctx.compoundKey().partitionKey().column().getText());
            for (CqlParser.ClusteringKeyContext ccol : ctx.compoundKey().clusteringKeyList().clusteringKey()) {
                model.addClusteringColumn(ccol.column().getText());
            }
        }
    }

    @Override
    public void exitSinglePrimaryKey(CqlParser.SinglePrimaryKeyContext ctx) {
        super.exitSinglePrimaryKey(ctx);
    }

    @Override
    public void exitCreateTable(CqlParser.CreateTableContext ctx) {
        model.saveTable(
            ctx.keyspace().OBJECT_NAME().getText(),
            ctx.table().OBJECT_NAME().getText(),
            textOf(ctx)
        );
    }

    private String textOf(ParserRuleContext ctx) {
        int startIndex = ctx.start.getStartIndex();
        int stopIndex = ctx.stop.getStopIndex();
        Interval interval = Interval.of(startIndex, stopIndex);
        String text = ctx.start.getInputStream().getText(interval);
        return text;
    }

    @Override
    public void enterColumnDefinition(CqlParser.ColumnDefinitionContext ctx) {
    }

    @Override
    public void exitColumnDefinition(CqlParser.ColumnDefinitionContext ctx) {
        model.saveColumnDefinition(
            ctx.column().getText(),
            ctx.dataType().getText(),
            ctx.primaryKeyColumn()!=null
        );
    }

    @Override
    public String toString() {
        return model.toString();
    }


    public CqlModel getModel() {
        return model;
    }

    public List<String> getErrors() {
        return model.getErrors();
    }

}