package org.isegodin.example.elastic.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.isegodin.example.elastic.search.dto.Identifier;

/**
 * @author i.segodin
 */
public class FakeDatabase<ID, T extends Identifier<ID>> {

    private final Map<ID, T> items = new HashMap<>();

    public FakeDatabase() {
    }

    public FakeDatabase(Collection<T> items) {
        for (T item : items) {
            add(item);
        }
    }

    public List<T> listAll() {
        return new ArrayList<>(items.values());
    }

    public void add(T item) {
        items.put(item.getId(), item);
    }
}
