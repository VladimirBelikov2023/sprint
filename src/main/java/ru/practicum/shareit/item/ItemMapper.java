package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@Service
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getLastBooking(),
                item.getNextBooking(),
                item.getComments()
        );
    }

    public static List<ItemDto> toLsItemDto(List<Item> ls) {
        List<ItemDto> itemDto = new ArrayList<>();
        for (Item o : ls) {
            itemDto.add(ItemMapper.toItemDto(o));
        }
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .lastBooking(itemDto.getLastBooking())
                .nextBooking(itemDto.getNextBooking())
                .comments(itemDto.getComments()).build();
    }

}
