package senac.tsi.books.config;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.List;
import java.util.function.Function;

public final class PagedModelBuilder {

    private PagedModelBuilder() {}

    public static <T> PagedModel<EntityModel<T>> from(Page<T> page, Function<T, EntityModel<T>> modelFactory) {
        List<EntityModel<T>> items = page.getContent().stream()
                .map(modelFactory)
                .toList();

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                page.getSize(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return PagedModel.of(items, metadata);
    }
}
