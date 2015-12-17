package org.matonto.rdf.core.impl.sesame;

import aQute.bnd.annotation.component.Component;
import org.matonto.rdf.api.Model;
import org.matonto.rdf.api.ModelFactory;
import org.matonto.rdf.api.Namespace;
import org.matonto.rdf.api.Statement;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;

@Component(provide = ModelFactory.class,
        properties = {
                "service.ranking:Integer=20",
                "implType=hash"
        })
public class LinkedHashModelFactoryService extends AbstractModelFactory {

    @Override
    public Model createModel() {
        return new LinkedHashModel();
    }

    @Override
    public Model createModel(@Nonnull Set<Namespace> namespaces, @Nonnull Collection<@Nonnull ? extends Statement> c) {
        Model finalModel = new LinkedHashModel(c.size());
        finalModel.addAll(c);
        namespaces.forEach(finalModel::setNamespace);
        return finalModel;
    }
}