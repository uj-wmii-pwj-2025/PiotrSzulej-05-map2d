package uj.wmii.pwj.map2d;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Map2DImplementation<R, C, V> implements Map2D<R, C, V>
{

    public Map<R, Map<C, V>> map;

    public Map2DImplementation()
    {
        map = new HashMap<>();
    }

    private void validation(R rowKey, C columnKey)
    {
        if (rowKey == null)
        {
            throw new NullPointerException("rowKey");
        }
        if (columnKey == null)
        {
            throw new NullPointerException("columnKey");
        }
    }

    @Override
    public V put(R rowKey, C columnKey, V value)
    {
        validation(rowKey, columnKey);
        Map<C, V> row = map.computeIfAbsent(rowKey, k -> new HashMap<C, V>());
        return row.put(columnKey, value);
    }

    @Override
    public V get(R rowKey, C columnKey)
    {
        if (rowKey == null || columnKey == null)
        {
            return null;
        }
        Map<C, V> row = map.get(rowKey);
        if (row == null)
        {
            return null;
        }
        return row.get(columnKey);
    }

    @Override
    public V getOrDefault(R rowKey, C columnKey, V defaultValue)
    {
        if (rowKey == null || columnKey == null)
        {
            return defaultValue;
        }
        if (!containsKey(rowKey, columnKey))
        {
            return defaultValue;
        }
        return get(rowKey, columnKey);
    }

    @Override
    public V remove(R rowKey, C columnKey)
    {
        if (rowKey == null || columnKey == null)
        {
            return null;
        }
        Map<C, V> row = map.get(rowKey);
        if (row == null)
        {
            return null;
        }
        V value = row.remove(columnKey);
        map.remove(rowKey);
        return value;
    }

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public boolean nonEmpty()
    {
        return !map.isEmpty();
    }

    @Override
    public int size() {
        int s = 0;
        for (Map<C, V> row : map.values())
        {
            s += row.size();
        }
        return s;
    }

    @Override
    public void clear()
    {
        map.clear();
    }

    @Override
    public Map<C, V> rowView(R rowKey)
    {
        if (rowKey == null)
        {
            return Collections.emptyMap();
        }
        Map<C, V> row = map.get(rowKey);
        if (row == null || row.isEmpty())
        {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap<C, V>(row));
    }

    @Override
    public Map<R, V> columnView(C columnKey)
    {
        if (columnKey == null)
        {
            return Collections.emptyMap();
        }
        Map<R, V> result = new HashMap<R, V>();
        for (R rowKey : map.keySet())
        {
            Map<C, V> row = map.get(rowKey);
            result.put(rowKey, row.get(columnKey));
        }
        return Collections.unmodifiableMap(result);
    }

    @Override
    public boolean containsValue(V value)
    {
        for (Map<C, V> row : map.values())
        {
            for (V v : row.values())
            {
                if (v == null)
                {
                    if (value == null) return true;
                }
                else
                {
                    if (v.equals(value)) return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsKey(R rowKey, C columnKey)
    {
        if (rowKey == null || columnKey == null)
        {
            return false;
        }
        Map<C, V> row = map.get(rowKey);
        if(row != null && row.containsKey(columnKey))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean containsRow(R rowKey)
    {
        if (rowKey == null)
        {
            return false;
        }
        Map<C, V> row = map.get(rowKey);
        if(row != null && !row.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean containsColumn(C columnKey)
    {
        if (columnKey == null)
        {
            return false;
        }
        for (Map<C, V> row : map.values())
        {
            if (row != null && row.containsKey(columnKey))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<R, Map<C, V>> rowMapView()
    {
        if (map.isEmpty())
        {
            return Collections.emptyMap();
        }
        Map<R, Map<C, V>> copy = new HashMap<R, Map<C, V>>();
        for (R rowKey : map.keySet())
        {
            Map<C, V> row = map.get(rowKey);
            Map<C, V> rowCopy = Collections.unmodifiableMap(new HashMap<>(row));
            copy.put(rowKey, rowCopy);
        }
        return Collections.unmodifiableMap(copy);
    }

    @Override
    public Map<C, Map<R, V>> columnMapView()
    {
        if (map.isEmpty())
        {
            return Collections.emptyMap();
        }
        Map<C, Map<R, V>> columns = new HashMap<C, Map<R, V>>();
        for (R r : map.keySet())
        {
            Map<C, V> row = map.get(r);
            if (row == null)
            {
                continue;
            }
            for (C c : row.keySet())
            {
                Map<R, V> m = columns.get(c);
                if (m == null)
                {
                    m = new HashMap<R, V>();
                    columns.put(c, m);
                }
                m.put(r, row.get(c));
            }
        }
        Map<C, Map<R, V>> result = new HashMap<C, Map<R, V>>();
        for (Map.Entry<C, Map<R, V>> e : columns.entrySet())
        {
            result.put(e.getKey(), Collections.unmodifiableMap(new HashMap<R, V>(e.getValue())));
        }
        return Collections.unmodifiableMap(result);
    }

    @Override
    public Map2D<R, C, V> fillMapFromRow(Map<? super C, ? super V> target, R rowKey)
    {
        if (rowKey == null)
        {
            return this;
        }
        Map<C, V> row = map.get(rowKey);
        if (row != null && !row.isEmpty())
        {
            target.putAll(row);
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> fillMapFromColumn(Map<? super R, ? super V> target, C columnKey)
    {
        if (columnKey == null)
        {
            return this;
        }
        for (R rowKey : map.keySet())
        {
            Map<C, V> row = map.get(rowKey);
            if (row != null && row.containsKey(columnKey))
            {
                target.put(rowKey, row.get(columnKey));
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAll(Map2D<? extends R, ? extends C, ? extends V> source)
    {
        if (source == null)
        {
            return this;
        }
        Map<? extends R, ? extends Map<? extends C, ? extends V>> rows = source.rowMapView();
        for (R r : rows.keySet())
        {
            Map<? extends C, ? extends V> row = rows.get(r);
            if (row == null)
            {
                continue;
            }
            for (C c : row.keySet())
            {
                put(r, c, row.get(c));
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToRow(Map<? extends C, ? extends V> source, R rowKey)
    {
        if (rowKey == null)
        {
            return this;
        }
        for (C c : source.keySet())
        {
            put(rowKey, c, source.get(c));
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToColumn(Map<? extends R, ? extends V> source, C columnKey)
    {
        if (columnKey == null)
        {
            return this;
        }
        for (R r : source.keySet())
        {
            put(r, columnKey, source.get(r));
        }
        return this;
    }

    @Override
    public <R2, C2, V2> Map2D<R2, C2, V2> copyWithConversion(
            Function<? super R, ? extends R2> rowFunction,
            Function<? super C, ? extends C2> columnFunction,
            Function<? super V, ? extends V2> valueFunction)
    {
        Map2DImplementation<R2, C2, V2> target = new Map2DImplementation<R2, C2, V2>();
        for (R r : map.keySet())
        {
            R2 r2 = rowFunction.apply(r);
            if (r2 == null)
            {
                continue;
            }
            Map<C, V> row = map.get(r);
            if (row == null)
            {
                continue;
            }
            for (C c : row.keySet())
            {
                C2 c2 = columnFunction.apply(c);
                if (c2 == null)
                {
                    continue;
                }
                V v = row.get(c);
                V2 v2 = valueFunction.apply(v);
                target.put(r2, c2, v2);
            }
        }
        return target;
    }
}
