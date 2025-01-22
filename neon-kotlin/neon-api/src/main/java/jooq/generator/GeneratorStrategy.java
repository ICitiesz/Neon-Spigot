package jooq.generator;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

public class GeneratorStrategy extends DefaultGeneratorStrategy {
    @Override
    public String getJavaClassName(Definition definition, Mode mode) {
        if (!(mode == Mode.POJO || mode == Mode.DAO)) return super.getJavaClassName(definition, mode);

        return super.getJavaClassName(definition, mode).substring(2);
    }
}
