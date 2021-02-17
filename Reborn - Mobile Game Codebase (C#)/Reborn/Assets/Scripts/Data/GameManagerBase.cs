using System;
using UnityEngine;
using UnityEngine.Audio;

public abstract class GameManagerBase<TGameManager, TDataStore> : PersistentSingleton<TGameManager> 
    where TDataStore : GameDataStoreBase, new ()
    where TGameManager : GameManagerBase<TGameManager, TDataStore>
{
    /// <summary>
    /// File name saved game 
    /// </summary>
    const string k_SavedGameFile = "reborn_save";

    /// <summary>
    /// Reference to audio mixer for volume changing
    /// </summary>
    public AudioMixer gameMixer;

    /// <summary>
    /// Master volume parameter on the mixer
    /// </summary>
    public string masterVolumeParameter;

    /// <summary>
    /// SFX Volume parameter on the mixer
    /// </summary>
    public string sfxVolumeParameter;

    /// <summary>
    /// Music Volume parameter on the mixer
    /// </summary>
    public string musicVolumeParameter;

    /// <summary>
    /// The Serialization Implementation for persistence
    /// </summary>
    protected JsonSaver<TDataStore> m_DataSaver;

    /// <summary>
    /// The object used for persistence
    /// </summary>
    protected TDataStore m_DataStore;

    /// <summary>
    /// Retrieve volumes from data store
    /// </summary>
    /// <param name="master"></param>
    /// <param name="sfx"></param>
    /// <param name="music"></param>
    public virtual void GetVolumes(out float master, out float sfx, out float music)
    {
        master = m_DataStore.masterVolume;
        sfx = m_DataStore.sfxVolume;
        music = m_DataStore.musicVolume;
    }

    /// <summary>
    /// Retrieve touch sensitivity from data store
    /// </summary>
    /// <param name="touchSensitivity"></param>
    public virtual void GetTouchSensitivity(out float touchSensitivity)
    {
        touchSensitivity = m_DataStore.touchSensitivity;
    }

    /// <summary>
    /// Set and persist game volumes
    /// </summary>
    /// <param name="master"></param>
    /// <param name="sfx"></param>
    /// <param name="music"></param>
    /// <param name="save"></param>
    public virtual void SetVolumes(float master, float sfx, float music, bool save)
    {
        if (gameMixer == null)
        {
            return;
        }

        if (masterVolumeParameter != null)
        {
            gameMixer.SetFloat(masterVolumeParameter, LogarithmicDbTransform(Mathf.Clamp01(master))); 
        }
        if (sfxVolumeParameter != null)
        {
            gameMixer.SetFloat(sfxVolumeParameter, LogarithmicDbTransform(Mathf.Clamp01(sfx)));
        }
        if (musicVolumeParameter != null)
        {
            gameMixer.SetFloat(musicVolumeParameter, LogarithmicDbTransform(Mathf.Clamp01(music)));
        }

        if (save)
        {
            m_DataStore.masterVolume = master;
            m_DataStore.sfxVolume = sfx;
            m_DataStore.musicVolume = music;
            SaveData();
        }
    }

    /// <summary>
    /// Set and persist the touch sensitivity
    /// </summary>
    /// <param name="touchSensitivity">value to be set and persisted </param>
    public virtual void SetTouchSensitivity(float touchSensitivity, bool save)
    {
        if (save)
        {
            m_DataStore.touchSensitivity = touchSensitivity;
            SaveData();
        }
    }

    /// <summary>
    /// Load data
    /// </summary>
    protected override void Awake()
    {
        base.Awake();
        LoadData();
    }

    /// <summary>
    /// Initialize volumes
    /// </summary>
    protected virtual void Start()
    {
        SetVolumes(m_DataStore.masterVolume, m_DataStore.sfxVolume, m_DataStore.musicVolume, false);
        SetTouchSensitivity(m_DataStore.touchSensitivity, false);
    }

    /// <summary>
    /// Set up persistence
    /// </summary>
    protected void LoadData()
    {
#if UNITY_EDITOR
        m_DataSaver = new JsonSaver<TDataStore>(k_SavedGameFile); 
#else
        m_DataSaver = new EncryptedJsonSaver<TDataStore>(k_SavedGameFile);
#endif
        try
        {
            if (!m_DataSaver.Load(out m_DataStore))
            {
                m_DataStore = new TDataStore();
                SaveData();
            }
        }
        catch (Exception)
        {
            Debug.Log("Failed to load data, resetting");
            m_DataStore = new TDataStore();
            SaveData();
        }
    }

    /// <summary>
    /// Saves the game
    /// </summary>
    protected virtual void SaveData()
    {
        m_DataSaver.Save(m_DataStore);
    }

    /// <summary>
    /// Transform volume from linear to logarithmic
    /// </summary>
    /// <param name="volume"></param>
    /// <returns></returns>
    protected static float LogarithmicDbTransform(float volume)
    {
        volume = (Mathf.Log(89 * volume + 1) / Mathf.Log(90)) * 80;
        return volume - 80;
    }

}
