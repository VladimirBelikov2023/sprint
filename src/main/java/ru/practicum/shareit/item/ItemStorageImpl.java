package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemStorageImpl implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 1;

    @Override
    public ItemDto createItem(Item item) {
        item.setId(id);
        id++;
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto patchItem(Item item, long ownerId) {
        Item itemBase = items.get(item.getId());
        if (itemBase == null) {
            throw new NoObjectException("Item не найден в базе");
        }
        if (items.get(item.getId()).getOwner().getId() != ownerId) {
            throw new NoObjectException("Это не ваша вещь");
        }
        Item originItem = update(item, itemBase);
        items.put(originItem.getId(), originItem);
        return ItemMapper.toItemDto(originItem);
    }

    @Override
    public ItemDto getItem(long id) {
        Item item = items.get(id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItems(long id) {
        List<ItemDto> itemList = new ArrayList<>();
        for (Item o : items.values()) {
            if (o.getOwner().getId() == id) {
                ItemDto item = ItemMapper.toItemDto(o);
                itemList.add(item);
            }
        }
        return itemList;
    }

    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> itemList = new ArrayList<>();
        if (text == null || text.isEmpty() || text.isBlank()) {
            return itemList;
        }
        for (Item o : items.values()) {
            if ((o.getName().toLowerCase().contains(text.toLowerCase()) || o.getDescription().toLowerCase().contains(text.toLowerCase())) && o.getAvailable()) {
                ItemDto item = ItemMapper.toItemDto(o);
                itemList.add(item);
            }
        }
        return itemList;
    }

    private Item update(Item item, Item originItem) {
        if (item.getName() != null) {
            originItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            originItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            originItem.setAvailable(item.getAvailable());
        }
        return originItem;
    }
}
