using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Manages what impact effect should play when a bullet hits the surface of an object
/// TODO : Instantiate this behaviour through game manager
/// </summary>
public class ImpactManager : Singleton<ImpactManager>
{
    [System.Serializable]
    public class ImpactSetting
    {
        public ParticleSystem ParticlePrefab;
        public AudioClip ImpactSound;
        public Material TargetMaterial;
    }

    /// <summary>
    /// TODO : Move this to UIManager
    /// </summary>
    public Transform PopupText;

    /// <summary>
    /// Default Impact effect 
    /// </summary>
    public ImpactSetting DefaultSetting;

    /// <summary>
    /// Specified Impact settings
    /// </summary>
    public ImpactSetting[] ImpactSettings;

    private Dictionary<Material, ImpactSetting> m_SettingLookup = new Dictionary<Material, ImpactSetting>();

    private void Start()
    {
        PoolManager.instance.Initialize(DefaultSetting.ParticlePrefab, 32);
        foreach(var impactSetting in ImpactSettings)
        {
            PoolManager.instance.Initialize(impactSetting.ParticlePrefab, 32);
            m_SettingLookup.Add(impactSetting.TargetMaterial, impactSetting);
        }
    }

    /// <summary>
    /// Play impact effect
    /// </summary>
    /// <param name="position"></param>
    /// <param name="normal"></param>
    /// <param name="material">Determines what impact to play</param>
    public void PlayImpact(Vector3 position, Vector3 normal, Material material = null)
    {
        ImpactSetting setting = null;
        if (material == null || !m_SettingLookup.TryGetValue(material, out setting))
        {
            setting = DefaultSetting;
        }

        var system = PoolManager.instance.GetInstance<ParticleSystem>(setting.ParticlePrefab);
        system.gameObject.transform.position = position;
        system.gameObject.transform.forward = normal;
        system.gameObject.SetActive(true);
        system.Play();

        var source = AudioManager.GetWorldSFXSource();
        source.transform.position = position;
        source.pitch = Random.Range(0.7f, 1.2f);
        source.volume = 0.1f;
        source.PlayOneShot(setting.ImpactSound);
    }
}
