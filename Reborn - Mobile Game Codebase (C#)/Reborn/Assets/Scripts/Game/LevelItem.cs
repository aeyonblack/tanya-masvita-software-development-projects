using System;
using UnityEngine;

/// <summary>
/// Describes the basic level attributes
/// </summary>
[Serializable]
public class LevelItem
{
    /// <summary>
    /// Level ID used in persistence
    /// </summary>
    public string id;

    /// <summary>
    /// Human readable level name
    /// </summary>
    public string name;

    /// <summary>
    /// Level Image
    /// </summary>
    public Texture image;

    /// <summary>
    /// The description of the level
    /// </summary>
    public string description;

    /// <summary>
    /// Time spent in level
    /// </summary>
    public float time;

    /// <summary>
    /// The name of the scene to load
    /// </summary>
    public string sceneName;

    /// <summary>
    /// True if the level is locked
    /// </summary>
    public bool Locked;
}
