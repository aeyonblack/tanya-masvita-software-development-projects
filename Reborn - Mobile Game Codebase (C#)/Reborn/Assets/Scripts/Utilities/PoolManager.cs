using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Simple ring buffer style pool system. Not the best implementation
/// but will suffice for objects with a smaller life span
/// </summary>
public class PoolManager : Singleton<PoolManager>
{
    /// <summary>
    /// Creates a global game object
    /// OOkay this is bullshit
    /// </summary>
    public static void Create()
    {
        GameObject obj = new GameObject("PoolManager");
        instance = obj.AddComponent<PoolManager>();
    }

    /// <summary>
    /// Dictionary of pools (this implementation is subject to change)
    /// </summary>
    private Dictionary<Object, Queue<Object>> m_Pools = new Dictionary<Object, Queue<Object>>();

    /// <summary>
    /// Initialize the pool
    /// </summary>
    /// <param name="prefab">Poolable object prefab</param>
    /// <param name="size">size of pool</param>
    public void Initialize(UnityEngine.Object prefab, int size)
    {
        if (m_Pools.ContainsKey(prefab))
        {
            return;
        }

        Queue<Object> queue = new Queue<Object>();

        for (int i = 0; i < size; ++i)
        {
            var o = Instantiate(prefab);
            SetActive(o, false);
            queue.Enqueue(o);
        }
        m_Pools[prefab] = queue;
    }

    /// <summary>
    /// Returns instance of the poolable object
    /// </summary>
    /// <typeparam name="T">type of the poolable object</typeparam>
    /// <param name="prefab">poolable object prefab</param>
    /// <returns></returns>
    public T GetInstance<T>(Object prefab) where T : Object
    {
        Queue<Object> queue;
        if (m_Pools.TryGetValue(prefab, out queue))
        {
            Object obj;

            if (queue.Count > 0)
            {
                obj = queue.Dequeue();
            }
            else
            {
                obj = Instantiate(prefab);
            }

            SetActive(obj, true);
            queue.Enqueue(obj);

            return obj as T;
        }

        return null;
    }

    /// <summary>
    /// Activate poolable object prefab
    /// </summary>
    /// <param name="obj">poolable object</param>
    /// <param name="active">either true or false</param>
    private static void SetActive(Object obj, bool active)
    {
        GameObject go = null;
        if (obj is Component component)
        {
            go = component.gameObject;
        }
        else
        {
            go = obj as GameObject;
        }
        go.SetActive(active);
    }

}
