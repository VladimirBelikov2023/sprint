package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") int id, @RequestBody @Validated(Create.class) ItemDto item) {
        return itemService.createItem(item, id);
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") int id, @RequestBody @Valid CommentDto comment, @PathVariable int itemId) {
        return itemService.createComment(id, itemId, comment);
    }

    @PatchMapping("{id}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") int ownerId, @RequestBody @Validated(Update.class) ItemDto item, @PathVariable int id) {
        return itemService.patchItem(item, id, ownerId);
    }

    @GetMapping("{id}")
    public ItemDto getItem(@PathVariable int id, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemService.getItem(id, ownerId);
    }


    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") int id, @PathVariable(required = false) Integer itemId) {
        if (itemId == null) {
            itemId = 0;
        }
        return itemService.getItems(id, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

}
