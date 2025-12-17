package jooq.generator;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;
import org.jooq.meta.TableDefinition;

public class GeneratorStrategy extends DefaultGeneratorStrategy {
    @Override
    public String getJavaClassName(Definition definition, Mode mode) {
        if ((mode == Mode.DEFAULT) && definition instanceof TableDefinition) return super.getJavaClassName(definition, mode) + "Table";

        if (!(mode == Mode.POJO || mode == Mode.DAO)) return super.getJavaClassName(definition, mode);

        return super.getJavaClassName(definition, mode).substring(2);
    }

    @Override
    public String getJavaIdentifier(Definition definition) {
        if (definition instanceof TableDefinition) return super.getJavaIdentifier(definition) + "_TABLE";

        return super.getJavaIdentifier(definition);
    }
}
