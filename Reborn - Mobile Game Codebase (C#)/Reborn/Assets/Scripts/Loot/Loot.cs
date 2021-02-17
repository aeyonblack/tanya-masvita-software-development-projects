using UnityEngine;
using UnityEngine.AI;

/// <summary>
/// Basic definitions of each loot item
/// </summary>
public class Loot : MonoBehaviour
{
    #region Fields 

    public CharacterData Player;
    public LootItem Item;
    public AudioClip PickupClip;
    public float AggroRange;

    private static float m_AnimationTime = 0.5f;
    private float m_AnimationTimer = 0f;
    private Vector3 m_OriginalPosition;
    private Vector3 m_TargetPoint;
    private Vector3 m_Rotation;

    #endregion

    #region Monobehaviours

    private void Awake()
    {
        m_OriginalPosition = transform.position;
        m_TargetPoint = transform.position;
        m_AnimationTimer = m_AnimationTime - 0.1f;
        m_Rotation = new Vector3(0, 50f, 0);
    }

    private void Start()
    {
        CreateWorldRepresentation();
    }

    private void Update()
    {
        if (m_AnimationTimer < m_AnimationTime)
        {
            m_AnimationTimer += Time.deltaTime;
            float ratio = Mathf.Clamp01(m_AnimationTimer / m_AnimationTime);
            Vector3 currentPosition = Vector3.Lerp(m_OriginalPosition, m_TargetPoint, ratio);
            currentPosition.y = currentPosition.y + Mathf.Sin(ratio * Mathf.PI) * 1.5f;

            transform.position = currentPosition;

            // Wait for the animation to end before showing the Loot UI
            if (m_AnimationTimer >= m_AnimationTime)
            {
                //LootUI.Instance.NewLoot(this);
            }
        }

        if (Player != null && Vector3.Distance(transform.position, Player.transform.position) < AggroRange)
        {
            TryPickup();
        }
    }

    #endregion

    /// <summary>
    /// Spawn this loot item 
    /// </summary>
    /// <param name="position">Position to spawn object</param>
    public void Spawn(Vector3 position)
    {
        m_OriginalPosition = position;
        transform.position = position;

        Vector3 targetPos;
        if (!RandomPoint(transform.position, 2.0f, out targetPos))
            targetPos = transform.position;

        m_TargetPoint = targetPos;
        m_AnimationTimer = 0.0f;

        Helpers.RecursiveLayerChange(gameObject.transform, LayerMask.NameToLayer("Collectable"));
    }

    private bool RandomPoint(Vector3 center, float range, out Vector3 result)
    {
        for (int i = 0; i < 30; i++)
        {
            Vector3 randomPoint = center + Random.insideUnitSphere * range;
            NavMeshHit hit;
            if (NavMesh.SamplePosition(randomPoint, out hit, 1.0f, NavMesh.AllAreas))
            {
                result = hit.position;
                return true;
            }
        }
        result = Vector3.zero;
        return false;
    }

    /// <summary>
    /// Try pick up the loot item
    /// </summary>
    protected virtual void TryPickup()
    {
        if (Item != null)
        {
            Player.Inventory.AddItem(Item);
        }

        var source = AudioManager.GetWorldSFXSource();
        source.transform.position = transform.position;
        if (PickupClip != null)
        {
            source.PlayOneShot(PickupClip);
        }
        if (Item != null)
        {
            Player.InventoryWindow.Load(Player); // TODO : Update properly (This works)
            Player.HotbarWindow.Load();
        }
        Destroy(gameObject);
    }

    /// <summary>
    /// Create a world object for this item 
    /// </summary>
    private void CreateWorldRepresentation()
    {
        if (Item != null)
        {
            if (Item.worldObjectPrefab != null)
            {
                // Use the 3D object
                Vector3 pos = new Vector3(0f, 1f, 0f);
                var loot = Instantiate(Item.worldObjectPrefab, transform, false);
                loot.transform.localPosition = pos;
                Helpers.RecursiveLayerChange(loot.transform, LayerMask.NameToLayer("Collectable"));
            }
            else
            {
                // Create a billboard using the item sprite
                GameObject billboard = new GameObject("ItemBillboard");
                billboard.transform.SetParent(transform, false);
                billboard.transform.localPosition = Vector3.up * 0.3f;
                billboard.layer = LayerMask.NameToLayer("Collectable");

                var renderer = billboard.AddComponent<SpriteRenderer>();
                renderer.sharedMaterial = ResourceManager.Instance.BillboardMaterial;
                renderer.sprite = Item.itemSprite;

                var rect = Item.itemSprite.rect;
                float maxSize = rect.width > rect.height ? rect.width : rect.height;
                float scale = Item.itemSprite.pixelsPerUnit / maxSize;

                billboard.transform.localScale = scale * Vector3.one * 0.5f;

                var bc = billboard.AddComponent<BoxCollider>();
                bc.size = new Vector3(0.5f, 0.5f, 0.5f) * (1.0f / scale);
            }
        }
    }

    private void OnDrawGizmos()
    {
        Gizmos.color = Color.blue;
        Gizmos.DrawWireSphere(transform.position, AggroRange);
    }
}
