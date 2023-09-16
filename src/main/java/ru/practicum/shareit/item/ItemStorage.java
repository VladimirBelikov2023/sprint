package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    ItemDto createItem(Item item);

    ItemDto patchItem(Item item, long ownerId);

    ItemDto getItem(long id);

    List<ItemDto> getItems(long id);

    List<ItemDto> search(String text);
}
