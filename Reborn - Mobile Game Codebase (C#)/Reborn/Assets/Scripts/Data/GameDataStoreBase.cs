using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Base game data store 
/// </summary>
public abstract class GameDataStoreBase : IDataStore
{
    public float masterVolume = 1;

    public float sfxVolume = 1;

    public float musicVolume = 1;

    /// <summary>
    /// Default touch sensitivity
    /// </summary>
    public float touchSensitivity = 0.2f;

    /// <summary>
    /// Called just after save
    /// </summary>
    public abstract void PreSave();

    /// <summary>
    /// Called just after load
    /// </summary>
    public abstract void PostLoad();
}
