package ru.practicum.shareit.item.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;


import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long userId);

    @Query("""
           select i from Item i
           where (upper(i.name) like upper(concat('%', ?1, '%'))
              or upper(i.description) like upper(concat('%', ?1, '%')))
             and i.available = true
           """)
    List<Item> findAvailableByText(String text);

    List<Item> findAllByRequestIdIn(Collection<Long> requestIds);

    List<Item> findAllByRequestId(Long requestId);
}
