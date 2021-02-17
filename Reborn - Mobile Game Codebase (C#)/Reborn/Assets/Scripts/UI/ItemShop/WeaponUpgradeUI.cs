using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Displays weapon upgrade stats
/// </summary>
public class WeaponUpgradeUI : Singleton<WeaponUpgradeUI>
{
    /// <summary>
    /// Indicates the damage for a selected weapon
    /// </summary>
    public Slider DamageSlider;

    /// <summary>
    /// Indicated the fire rate for a selected weapon
    /// </summary>
    public Slider FireRateSlider;

    /// <summary>
    /// Indicated the clip size of a selected weapon
    /// </summary>
    public Slider ClipSizeSlider;

    [HideInInspector] public LootItem item;

    private float damage;

    private float fireRate;

    private float clipSize;

    private bool isAnimating;

    private WeaponConfiguration weapon;

    private void Start()
    {
        damage = 0;
        fireRate = 0;
        clipSize = 0;
        isAnimating = true;
        weapon = item as WeaponConfiguration;
    }

    private void Update()
    {
        if (isAnimating)
        {
            if (item == null)
            {
                Debug.LogWarning("[WeaponUI] Weapon is null");
                return;
            }
            if (damage < weapon.damage)
            {
                damage += 0.01f;
            }
            DamageSlider.value = damage;

            if (fireRate < (0.5 - weapon.fireRate))
            {
                fireRate += 0.01f;
            }
            FireRateSlider.value = fireRate;

            if (clipSize < weapon.Clip.size)
            {
                clipSize += 0.01f;
            }
            ClipSizeSlider.value = clipSize;
            
            isAnimating = damage >= weapon.damage && fireRate >= (0.5 - weapon.fireRate) && clipSize >= weapon.Clip.size;
        }
    }
}
