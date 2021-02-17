using UnityEngine;
using TMPro;


/// <summary>
/// Displays damage when enemy is hit
/// </summary>
public class DamagePopup : MonoBehaviour
{
    public AudioClip CriticalHit;

    private TextMeshProUGUI textMesh;
    private Color textColor;
    private float timer;
    private const float DISAPPEAR_TIMER_MAX = 0.6f;
    private float disappearSpeed = 1.5f;
    private Vector3 moveVector;
    [SerializeField]private float moveSpeed = 20f;

    private static int sortingOrder;

    public static DamagePopup Create(Vector3 position, float damage, bool isCriticalHit, CharacterData player, bool isXp)
    {
        Transform obj = Instantiate(ImpactManager.instance.PopupText, position, Quaternion.identity);
        DamagePopup popup = obj.GetComponent<DamagePopup>();
        popup.Setup(damage, isCriticalHit, player, isXp);

        return popup;
    }

    private void Awake()
    {
        textMesh = transform.GetComponent<TextMeshProUGUI>();
    }

    private void Update()
    {
        transform.position += moveVector * Time.deltaTime;
        moveVector -= moveVector * 10f * Time.deltaTime;

        if (timer > DISAPPEAR_TIMER_MAX * 0.5f)
        {
            transform.localScale += Vector3.one * 0.02f * Time.deltaTime;
        }
        else
        {
            transform.localScale -= Vector3.one * 0.02f * Time.deltaTime;
        }

        timer -= Time.deltaTime;

        if (timer < 0)
        {
            textColor.a -= disappearSpeed * Time.deltaTime;
            textMesh.color = textColor;
            if (textColor.a < 0)
                Destroy(gameObject);
        }
    }

    public void Setup(float value, bool isCriticalHit, CharacterData player, bool isXp)
    {
        transform.forward = player.transform.forward;

        if (!isXp)
        {
            textMesh.SetText(((int)value).ToString());
            if (isCriticalHit)
            {
                textMesh.fontSize = 70;
                textColor = new Color32(246, 13, 36, 255);
            }
            else
            {
                textMesh.fontSize = 55;
                textColor = new Color32(233, 213, 18, 255);
            }
        }
        else
        {
            textMesh.SetText("+" + value + "XP");
            textMesh.fontSize = 60;
            textColor = new Color32(208,18,222,255);
        }
        sortingOrder++;
        transform.gameObject.GetComponent<Canvas>().sortingOrder = sortingOrder;
        textMesh.color = textColor;
        timer = DISAPPEAR_TIMER_MAX;
        moveVector = new Vector3(Random.Range(1,1.5f), Random.Range(1,1.5f), Random.Range(1,1.5f)) * moveSpeed;
    }

    private void PlayCriticalHitAudio()
    {
        var source = AudioManager.GetWorldSFXSource();
        source.transform.position = transform.position;
        source.pitch = Random.Range(0.6f, 0.9f);
        source.PlayOneShot(CriticalHit);
    }

}
