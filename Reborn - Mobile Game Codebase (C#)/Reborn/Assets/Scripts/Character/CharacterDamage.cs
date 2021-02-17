using System;
using System.Collections;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Inflict damage on the player
/// </summary>
[RequireComponent(typeof(CharacterData))]
public class CharacterDamage : MonoBehaviour, IAttackable
{
    private CharacterData player;

    public ExtraLifeUI ExtraLifeUI;

    private EnemyController m_Attacker;

    public Action PlayerDead;

    private void Awake()
    {
        player = GetComponent<CharacterData>();
    }

    /// <summary>
    /// Called when the player is attacked, hit or damaged in some way
    /// </summary>
    /// <param name="attacker">usually the enemy</param>
    /// <param name="attack"></param>
    public void OnAttack(EnemyController attacker, Attack attack)
    {
        player.TakeDamage(attack);
        if (player.Stats.currentHealth <= 0)
        {
            StartCoroutine(MakeExtraLifeOffer(attacker));
        }
    }

    public IEnumerator MakeExtraLifeOffer(EnemyController attacker)
    {
        player.Death();
        ShowExtraLifeOffer();
        yield return new WaitForSeconds(5f);
        KillPlayer(attacker);
    }

    private void ShowExtraLifeOffer()
    {
        if (!ExtraLifeUI.gameObject.activeSelf)
        {
            ExtraLifeUI.gameObject.SetActive(true);
        }
    }

    private void KillPlayer(EnemyController attacker)
    {
        if (player.Stats.alive)
        {
            return;
        }

        PlayerDead?.Invoke();

        var destructibles = GetComponents(typeof(IDestructible));

        if (attacker)
        {
            foreach (IDestructible d in destructibles)
            {
                d.OnDestruction(player, attacker.Stats);
            }
        }

        if (ExtraLifeUI.gameObject.activeSelf)
        {
            ExtraLifeUI.gameObject.SetActive(false);
        }

        player.CurrentWeapon.PutAway();
    }
}
