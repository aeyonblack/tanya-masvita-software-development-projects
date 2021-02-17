using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Defines the behaviour and data of a spell attack
/// </summary>
[CreateAssetMenu(fileName = "New Spell", menuName = "Reborn/Attack/Magic Spell", order = 30)]
public class MagicSpell : AttackConfiguration
{
    public MagicProjectile ProjectilePrefab;
    public float ProjectileSpeed;

    public void Cast(EnemyController caster, Vector3 hotspot, Vector3 target, int layer)
    {
        MagicProjectile projectile = Instantiate(ProjectilePrefab, hotspot, Quaternion.identity);
        projectile.Launch(caster, target, ProjectileSpeed, Range);
        Helpers.RecursiveLayerChange(projectile.gameObject.transform, layer);
        projectile.ProjectileCollided += OnProjectileCollided;
    }

    private void OnProjectileCollided(EnemyController caster, CharacterData target)
    {
        if (caster == null || target == null)
            return;

        var attack = CreateAttack(caster.Stats, target.Stats);
        var attackables = target.GetComponentsInChildren(typeof(IAttackable));
        foreach (IAttackable a in attackables)
        {
            a.OnAttack(caster, attack);
        }
    }
}
