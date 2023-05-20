using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Serialization;

namespace Fergicide
{
	[CreateAssetMenu(fileName = "NewDfaultsConfig", menuName = "Fergicide/Dfaults/New Config")]
	public class DfaultsConfig : ScriptableObject
	{
		//public Texture2D texture;
		[Header("Mouse-over properties for info.")]
		[Tooltip("Allow config live update in Editor while playing.")]
		public bool liveEditInPlay;
		[Header("Seed")]
		[Tooltip("If Seed value is zero (random seed), preserve next generated seed.")]
		public bool saveRandomSeed;
		[Range(0, 1)]
		[Tooltip("0 = randomize")]
		public float seed = 0.123f;

		[Header("Color")]
		public Color bodyUpperColor = HexToColor("FF36C1");
		public Color bodyLowerColor = HexToColor("FF00B0");
		[Tooltip("Draw lower body color under others")]
		public bool bodyLowerUnder = true;
		public Color hatColor = HexToColor("FFFD00");
		[Tooltip("Draw hat/hair color under others")]
		public bool hatUnder = false;
		[Tooltip("Color Range (0-1): X=bottom, Y=top \nSet to 0 to remove")]
		public Vector2 edgeHeight = new Vector2(0.4f, 0);
		public Color eyeColorOuter = Color.white;
		public Color eyeColorPupil = Color.black;
		public Color mouthColor = Color.black;

		[Header("Material")]
		[Range(0, 1)]
		public float smoothness = 0.85f;
		[Range(0, 1)]
		public float metallic = 0;

		[Header("Face: Stretch/Move")]
		[Tooltip("Stretch: X=width, Y=height \nMove: Z=horiz, W=vert")]
		public Vector4 tilingAndOffset = new Vector4(0.05f, 0.1f, 0, 0);

		[Header("Eyes: Move")]
		[Tooltip("Move: X=horiz, Y=vert \n(Default values are for Capsule)")]
		public Vector2 scrollingOffset = new Vector2(-5.75f, -7.38f);

		[Header("Eyes: Size")]
		[Tooltip("Size: X=left eye, Y=right eye")]
		public Vector2 eyeSize = new Vector2(0.7f, 0.44f);

		[Header("Eyes: Blink speed")]
		[Range(0, 0.25f)]
		public float eyeBlinkSpeed = 0.02f;

		[Header("Eyes: X=vert dist; Y=horiz dist")]
		public Vector2 eyeDistance = new Vector2(1.7f, -0.04f);
		[Range(0, 1), Header("Fixed pupil size: when pupil range X&Y=0")]
		public float eyePupil = 0.1f;
		[Header("Pupil size range (0-1): X=min; Y=max; Z=energy")]
		public Vector4 eyePupilRange = new Vector4(0.05f, 0.2f, 0.5f, 2.71f);
		[Header("Mouth: X=horiz; Y=vert; Z=size; W=energy")]
		public Vector4 mouth = new Vector4(-5f, -6.21f, 0.19f, 2.25f);
		[Header("Mouth_2: X=horiz; Y=vert; Z=size; W=energy")]
		public Vector4 mouthShaper = new Vector4(-0.05f, 1.13f, 8.88f, 1.15f);

		[Header("Animation Speed: Eyes/Mouth (0-2)")]
		[Tooltip("Move: X=eye speed, Y=mouth speed")]
		public Vector2 animSpeed = new Vector2(1, 1);

		public static Color HexToColor(string hex)
		{
			hex = hex.Replace("0x", "");//in case the string is formatted 0xFFFFFF
			hex = hex.Replace("#", "");//in case the string is formatted #FFFFFF
			byte a = 255;//assume fully visible unless specified in hex
			byte r = byte.Parse(hex.Substring(0, 2), System.Globalization.NumberStyles.HexNumber);
			byte g = byte.Parse(hex.Substring(2, 2), System.Globalization.NumberStyles.HexNumber);
			byte b = byte.Parse(hex.Substring(4, 2), System.Globalization.NumberStyles.HexNumber);
			//Only use alpha if the string has enough characters
			if (hex.Length == 8)
			{
				a = byte.Parse(hex.Substring(4, 2), System.Globalization.NumberStyles.HexNumber);
			}
			return new Color32(r, g, b, a);
		}
	}
}
