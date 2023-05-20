using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace Fergicide
{
	[ExecuteInEditMode]
	public class DfaultsController : MonoBehaviour
	{
		public DfaultsConfig dfaultsConfig;
		public Material dfaultsMaterial;
		private enum Error { none, dfaultsConfig, dfaultsMaterial, dfaultsMaterialShader, dfaultsMaterialInstancing }
		private static int[] propertiesToID;
		private static readonly string[] propertyNames =
		{
			"_ipUpperColor",
			"_ipTilingAndOffset",
			"_ipGlossiness",
			"_ipMetallic",
			"_ipScrolling",
			"_ipSeed",
			"_ipEyeColorW",
			"_ipEyeColorP",
			"_ipShape",
			"_ipEyeDist",
			"_ipEyePupil",
			"_ipEyePupilRange",
			"_ipMouth",
			"_ipMouthShaper",
			"_ipMouthColor",
			"_ipAnimSpeed",
			"_ipLowerColor",
			"_ipEdgeHeight",
			"_ipEyeBlinkSpeed",
			"_ipHatColor",
			"_ipHatUnder",
			"_ipLowerUnder"
		};

		private new Renderer[] renderers;
		private MaterialPropertyBlock mpb;
		private bool liveEdit;
		private float dfaultsConfigSeed;

		private void Start()
		{
			SetupMaterial(this);
		}

#if UNITY_EDITOR
		private void Update()
		{
			// Live properties update only available in Editor mode.
			if (liveEdit) UpdateProperties(this);
		}
#endif

		private static void SetupMaterial(DfaultsController _this)
		{

			if (!SanityChecks(_this))
			{
				Debug.LogError("SetupMaterial error!  DfaultsControl script has been disabled.  Fix error and re-enable the script.");
				_this.enabled = false;
				return;
			}

			_this.liveEdit = _this.dfaultsConfig.liveEditInPlay;

			// Setup PropertiesToID array.
			if (propertiesToID == null) SetupPropertiesToID();

			_this.renderers = GetRenderers(_this);
			_this.mpb = new MaterialPropertyBlock();

			// Assign shader.
			for (int i = 0; i < _this.renderers.Length; i++)
				_this.renderers[i].sharedMaterial = _this.dfaultsMaterial;

			// Set up random properties.
			if (_this.dfaultsConfig.seed == 0 && _this.dfaultsConfig.saveRandomSeed) _this.dfaultsConfig.seed = Random.value;
			_this.dfaultsConfigSeed = (_this.dfaultsConfig.seed == 0) ? Random.value : _this.dfaultsConfig.seed;

			// PerRenderedData.
			UpdateProperties(_this);

			// Cleanup - for build, disable script once it has run.
#if UNITY_EDITOR
#else
			_this.enabled = false;
#endif
		}

#if UNITY_EDITOR
#else
		private void OnDisable()
		{
			mpb = null;
			renderers = null;
			dfaultsConfig = null;
			dfaultsMaterial = null;
		}
#endif

		private static void UpdateProperties(DfaultsController _this)
		{
			// PerRenderedData.
			for (int i = 0; i < _this.renderers.Length; i++)
				_this.renderers[i].GetPropertyBlock(_this.mpb);

			_this.mpb.SetColor(propertiesToID[0], _this.dfaultsConfig.bodyUpperColor);
			_this.mpb.SetVector(propertiesToID[1], _this.dfaultsConfig.tilingAndOffset);
			_this.mpb.SetFloat(propertiesToID[2], _this.dfaultsConfig.smoothness);
			_this.mpb.SetFloat(propertiesToID[3], _this.dfaultsConfig.metallic);
			_this.mpb.SetVector(propertiesToID[4], _this.dfaultsConfig.scrollingOffset);
			_this.mpb.SetFloat(propertiesToID[5], _this.dfaultsConfigSeed);
			_this.mpb.SetColor(propertiesToID[6], _this.dfaultsConfig.eyeColorOuter);
			_this.mpb.SetColor(propertiesToID[7], _this.dfaultsConfig.eyeColorPupil);
			_this.mpb.SetVector(propertiesToID[8], _this.dfaultsConfig.eyeSize);
			_this.mpb.SetVector(propertiesToID[9], _this.dfaultsConfig.eyeDistance);
			_this.mpb.SetFloat(propertiesToID[10], _this.dfaultsConfig.eyePupil);
			_this.mpb.SetVector(propertiesToID[11], _this.dfaultsConfig.eyePupilRange);
			_this.mpb.SetVector(propertiesToID[12], _this.dfaultsConfig.mouth);
			_this.mpb.SetVector(propertiesToID[13], _this.dfaultsConfig.mouthShaper);
			_this.mpb.SetVector(propertiesToID[14], _this.dfaultsConfig.mouthColor);
			_this.mpb.SetVector(propertiesToID[15], _this.dfaultsConfig.animSpeed);
			_this.mpb.SetColor(propertiesToID[16], _this.dfaultsConfig.bodyLowerColor);
			_this.mpb.SetVector(propertiesToID[17], _this.dfaultsConfig.edgeHeight);
			_this.mpb.SetFloat(propertiesToID[18], _this.dfaultsConfig.eyeBlinkSpeed);
			_this.mpb.SetColor(propertiesToID[19], _this.dfaultsConfig.hatColor);
			_this.mpb.SetFloat(propertiesToID[20], _this.dfaultsConfig.hatUnder ? 1 : 0);
			_this.mpb.SetFloat(propertiesToID[21], _this.dfaultsConfig.bodyLowerUnder ? 1 : 0);

			for (int i = 0; i < _this.renderers.Length; i++)
				_this.renderers[i].SetPropertyBlock(_this.mpb);
		}

		private static void SetupPropertiesToID()
		{
			propertiesToID = new int[propertyNames.Length];
			for (int i = 0; i < propertyNames.Length; i++)
				propertiesToID[i] = Shader.PropertyToID(propertyNames[i]);
		}

		private static bool SanityChecks(DfaultsController _this)
		{
			Error error = Error.none;
			if (_this.dfaultsConfig == null) error = Error.dfaultsConfig;
			else if (_this.dfaultsMaterial == null) error = Error.dfaultsMaterial;
			else if (_this.dfaultsMaterial.shader.name != "Fergicide/Dfaults") error = Error.dfaultsMaterialShader;
			else if (_this.dfaultsMaterial.enableInstancing == false) error = Error.dfaultsMaterialInstancing;

			switch (error)
			{
				case Error.dfaultsConfig:
					Debug.LogWarning("Must have a DfaultsConfig scriptable object assigned!"); break;
				case Error.dfaultsMaterial:
					Debug.LogWarning("Must have the Dfaults material assigned!"); break;
				case Error.dfaultsMaterialShader:
					Debug.LogWarning("Material must use the Dfaults shader!"); break;
				case Error.dfaultsMaterialInstancing:
					Debug.LogWarning("Dfaults material 'Instancing' checkbox must be ticked."); break;
			}

			if (error == Error.none) return true;
			else return false;
		}

		private static Renderer[] GetRenderers(DfaultsController _this)
		{
			Renderer[] result;
			if (_this.GetComponent<LODGroup>() == null)
				// No LODs.
				result = new Renderer[] { _this.GetComponent<Renderer>() };
			else
				// LODs, so get renderers from children.
				// Should be no renderer on parent gameobject.
				result = _this.GetComponentsInChildren<Renderer>();

			return result;
		}
	}
}