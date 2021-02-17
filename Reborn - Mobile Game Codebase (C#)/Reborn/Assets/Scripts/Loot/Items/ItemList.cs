using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using UnityEngine;

/// <summary>
/// Item configuration data
/// </summary>
[CreateAssetMenu(fileName = "ItemList", menuName = "Reborn/New Item List")]
public class ItemList : ScriptableObject, IList<ShopItem>, IDictionary<string, ShopItem>, ISerializationCallbackReceiver
{
    public ShopItem[] items;

    /// <summary>
    /// Cached dictionary of items by their ids
    /// </summary>
    private IDictionary<string, ShopItem> m_ItemDictionary;

    /// <summary>
    /// Returns the number of items
    /// </summary>
    public int Count
    {
        get { return items.Length; }
    }

    /// <summary>
    /// item list is always read-only
    /// </summary>
    public bool IsReadOnly
    {
        get { return true; }
    }

    /// <summary>
    /// Gets an item by index
    /// </summary>
    /// <param name="i"></param>
    /// <returns></returns>
    public ShopItem this[int i]
    {
        get { return items[i]; }
    }

    /// <summary>
    /// Gets an item by id
    /// </summary>
    /// <param name="key"></param>
    /// <returns></returns>
    public ShopItem this[string key]
    {
        get { return m_ItemDictionary[key]; }
    }

    /// <summary>
    /// Gets a collection of all item keys
    /// </summary>
    public ICollection<string> Keys
    {
        get { return m_ItemDictionary.Keys; }
    }

    /// <summary>
    /// Gets the index of a given item
    /// </summary>
    /// <param name="levelItem"></param>
    /// <returns></returns>
    public int IndexOf(ShopItem item)
    {

        if (item == null)
        {
            return -1;
        }

        for (int i = 0; i < items.Length; ++i)
        {
            if (items[i] == item)
            {
                return i;
            }
        }

        return -1;

    }

    /// <summary>
    /// Returns whether this item exists in the list
    /// </summary>
    /// <param name="item"></param>
    /// <returns></returns>
    public bool Contains(ShopItem item)
    {
        return IndexOf(item) >= 0;
    }

    /// <summary>
    /// Gets whether an item of the given id exists
    /// </summary>
    /// <param name="item"></param>
    /// <returns></returns>
    public bool ContainsKey(string key)
    {
        if (m_ItemDictionary == null)
        {
            Debug.Log("Item dictionary is null");
        }
        if (key == null)
        {
            Debug.Log("Key is null");
        }
        return m_ItemDictionary.ContainsKey(key);
    }

    /// <summary>
    /// Try get an item with the given key sojkk'
    /// </summary>
    /// <param name="key"></param>
    /// <param name="value"></param>
    /// <returns></returns>
    public bool TryGetValue(string key, out ShopItem value)
    {
        return m_ItemDictionary.TryGetValue(key, out value);
    }

    void ISerializationCallbackReceiver.OnBeforeSerialize()
    {
    }

    void ISerializationCallbackReceiver.OnAfterDeserialize()
    {
        m_ItemDictionary = items.ToDictionary(l => l.id);
    }

    ICollection<ShopItem> IDictionary<string, ShopItem>.Values
    {
        get { return m_ItemDictionary.Values; }
    }

    ShopItem IList<ShopItem>.this[int i]
    {
        get { return items[i]; }
        set { throw new NotSupportedException("Item List is read only"); }
    }

    ShopItem IDictionary<string, ShopItem>.this[string key]
    {
        get { return m_ItemDictionary[key]; }
        set { throw new NotSupportedException("Item List is read only"); }
    }

    void IList<ShopItem>.Insert(int index, ShopItem item)
    {
        throw new NotSupportedException("Item List is read only");
    }

    void IList<ShopItem>.RemoveAt(int index)
    {
        throw new NotSupportedException("Item List is read only");
    }

    void ICollection<ShopItem>.Add(ShopItem item)
    {
        throw new NotSupportedException("Item List is read only");
    }

    void ICollection<KeyValuePair<string, ShopItem>>.Add(KeyValuePair<string, ShopItem> item)
    {
        throw new NotSupportedException("Item List is read only");
    }

    void ICollection<KeyValuePair<string, ShopItem>>.Clear()
    {
        throw new NotSupportedException("Item List is read only");
    }

    bool ICollection<KeyValuePair<string, ShopItem>>.Contains(KeyValuePair<string, ShopItem> item)
    {
        return m_ItemDictionary.Contains(item);
    }

    void ICollection<KeyValuePair<string, ShopItem>>.CopyTo(KeyValuePair<string, ShopItem>[] array, int arrayIndex)
    {
        m_ItemDictionary.CopyTo(array, arrayIndex);
    }

    void ICollection<ShopItem>.Clear()
    {
        throw new NotSupportedException("Item List is read only");
    }

    void ICollection<ShopItem>.CopyTo(ShopItem[] array, int arrayIndex)
    {
        items.CopyTo(array, arrayIndex);
    }

    bool ICollection<ShopItem>.Remove(ShopItem item)
    {
        throw new NotSupportedException("Item List is read only");
    }

    public IEnumerator<ShopItem> GetEnumerator()
    {
        return ((IList<ShopItem>)items).GetEnumerator();
    }

    IEnumerator<KeyValuePair<string, ShopItem>> IEnumerable<KeyValuePair<string, ShopItem>>.GetEnumerator()
    {
        return m_ItemDictionary.GetEnumerator();
    }

    IEnumerator IEnumerable.GetEnumerator()
    {
        return items.GetEnumerator();
    }

    void IDictionary<string, ShopItem>.Add(string key, ShopItem value)
    {
        throw new NotSupportedException("Item List is read only");
    }

    bool ICollection<KeyValuePair<string, ShopItem>>.Remove(KeyValuePair<string, ShopItem> item)
    {
        throw new NotSupportedException("Item List is read only");
    }

    bool IDictionary<string, ShopItem>.Remove(string key)
    {
        throw new NotSupportedException("Item List is read only");
    }
}
