Shader "Fergicide/Dfaults"
{
	Properties
	{
		[HideInInspector] _MainTex("Albedo (RGB)", 2D) = "white" {}
	}
	SubShader
	{
		Tags { "RenderType"="Opaque" }
		LOD 200

		CGPROGRAM
		// Physically based Standard lighting model, and enable shadows on all light types
		#pragma surface surf Standard fullforwardshadows

		// Use shader model 3.0 target, to get nicer looking lighting
		#pragma target 3.0

		#define IF(a, b, c) lerp(b, c, step((fixed) (a), 0));


		sampler2D _MainTex;

		struct Input
		{
			float2 uv_MainTex;
		};


		// Add instancing support for this shader. You need to check 'Enable Instancing' on materials that use the shader.
		// See https://docs.unity3d.com/Manual/GPUInstancing.html for more information about instancing.
		// #pragma instancing_options assumeuniformscaling
		UNITY_INSTANCING_BUFFER_START(Props)
			// put more per-instance properties here
			UNITY_DEFINE_INSTANCED_PROP(fixed4, _ipUpperColor)
			UNITY_DEFINE_INSTANCED_PROP(half4, _ipTilingAndOffset)
			UNITY_DEFINE_INSTANCED_PROP(float, _ipGlossiness)
			UNITY_DEFINE_INSTANCED_PROP(float, _ipMetallic)
			UNITY_DEFINE_INSTANCED_PROP(half2, _ipScrolling)
			UNITY_DEFINE_INSTANCED_PROP(float, _ipSeed)
			UNITY_DEFINE_INSTANCED_PROP(fixed4, _ipEyeColorW)
			UNITY_DEFINE_INSTANCED_PROP(fixed4, _ipEyeColorP)
			UNITY_DEFINE_INSTANCED_PROP(half2, _ipShape)
			UNITY_DEFINE_INSTANCED_PROP(half2, _ipEyeDist)
			UNITY_DEFINE_INSTANCED_PROP(half, _ipEyePupil)
			UNITY_DEFINE_INSTANCED_PROP(half4, _ipEyePupilRange)
			UNITY_DEFINE_INSTANCED_PROP(half4, _ipMouth)
			UNITY_DEFINE_INSTANCED_PROP(fixed4, _ipMouthShaper)
			UNITY_DEFINE_INSTANCED_PROP(fixed4, _ipMouthColor)
			UNITY_DEFINE_INSTANCED_PROP(fixed2, _ipAnimSpeed)
			UNITY_DEFINE_INSTANCED_PROP(fixed4, _ipLowerColor)
			UNITY_DEFINE_INSTANCED_PROP(fixed2, _ipEdgeHeight)
			UNITY_DEFINE_INSTANCED_PROP(float, _ipEyeBlinkSpeed)
			UNITY_DEFINE_INSTANCED_PROP(fixed4, _ipHatColor)
			UNITY_DEFINE_INSTANCED_PROP(float, _ipHatUnder)
			UNITY_DEFINE_INSTANCED_PROP(float, _ipLowerUnder)
		UNITY_INSTANCING_BUFFER_END(Props)

		float afnoise(float x, float amplitude, float frequency, fixed speedFactor)
		{
			float y = sin(x * frequency);
			float t = _Time.y * frequency * speedFactor;
			y += sin(x * frequency * 2.1 + t) * 4.5;
			y += sin(x * frequency * 1.72 + t * 1.121) * 4.0;
			y += sin(x * frequency * 2.221 + t * 0.437) * 5.0;
			y += sin(x * frequency * 3.1122+ t * 4.269) * 2.5;
			y *= amplitude * 0.06;
			return sin(y);
		}

		float random(float2 st)
		{
			return frac(sin(dot(st.xy, float2(12.9898, 78.233))) * 43758.5453123);
		}

		float circle(float2 _st, float _radius){
			float radiusFactor = _radius * 0.01;
			float2 dist = _st * float2(0.5, 0.5);
			return 1. - smoothstep(
				_radius - radiusFactor,
				_radius + radiusFactor,
				dot(dist, dist) * 4.0
			);
		}

		void surf (Input IN, inout SurfaceOutputStandard o)
		{
			// Add instanced properties.
			fixed4 _UpperColor = UNITY_ACCESS_INSTANCED_PROP(Props, _ipUpperColor);
			half4 _TilingAndOffset = UNITY_ACCESS_INSTANCED_PROP(Props, _ipTilingAndOffset);
			float _Glossiness = UNITY_ACCESS_INSTANCED_PROP(Props, _ipGlossiness);
			float _Metallic = UNITY_ACCESS_INSTANCED_PROP(Props, _ipMetallic);
			half2 _Scrolling = UNITY_ACCESS_INSTANCED_PROP(Props, _ipScrolling);
			float _Seed = UNITY_ACCESS_INSTANCED_PROP(Props, _ipSeed);
			fixed4 _EyeColorW = UNITY_ACCESS_INSTANCED_PROP(Props, _ipEyeColorW);
			fixed4 _EyeColorP = UNITY_ACCESS_INSTANCED_PROP(Props, _ipEyeColorP);
			half2 _Shape = UNITY_ACCESS_INSTANCED_PROP(Props, _ipShape);
			half2 _EyeDist = UNITY_ACCESS_INSTANCED_PROP(Props, _ipEyeDist);
			half _EyePupil = UNITY_ACCESS_INSTANCED_PROP(Props, _ipEyePupil);
			half4 _EyePupilRange = UNITY_ACCESS_INSTANCED_PROP(Props, _ipEyePupilRange);
			half4 _Mouth = UNITY_ACCESS_INSTANCED_PROP(Props, _ipMouth);
			half4 _MouthShaper = UNITY_ACCESS_INSTANCED_PROP(Props, _ipMouthShaper);
			fixed4 _MouthColor = UNITY_ACCESS_INSTANCED_PROP(Props, _ipMouthColor);
			half2 _AnimSpeed = UNITY_ACCESS_INSTANCED_PROP(Props, _ipAnimSpeed);
			fixed4 _LowerColor = UNITY_ACCESS_INSTANCED_PROP(Props, _ipLowerColor);
			fixed2 _EdgeHeight = UNITY_ACCESS_INSTANCED_PROP(Props, _ipEdgeHeight);
			fixed _EyeBlinkSpeed = UNITY_ACCESS_INSTANCED_PROP(Props, _ipEyeBlinkSpeed);
			fixed4 _HatColor = UNITY_ACCESS_INSTANCED_PROP(Props, _ipHatColor);
			float _HatUnder = UNITY_ACCESS_INSTANCED_PROP(Props, _ipHatUnder);
			float _LowerUnder = UNITY_ACCESS_INSTANCED_PROP(Props, _ipLowerUnder);

			fixed noise = afnoise(23422.123 * _Seed + 0.1, 2, 0.00056, 1);
			fixed noise2 = afnoise(321.123, noise * 0.53, noise * 0.47323, _AnimSpeed.y);
			fixed noise2b = afnoise(321.123, noise * 0.35, noise * 0.32347, _AnimSpeed.y);
			fixed noise5 = afnoise(23.6345 * _Seed + 0.1, 1.163, 0.9, _AnimSpeed.x);
			fixed noise5b = afnoise(5243.347 * _Seed + 0.1, 0.678, 0.666, _AnimSpeed.x);

			float2 uv = IN.uv_MainTex + _TilingAndOffset.zw;
			
			// Animate pupil size.
			fixed doEyePupil = IF(
				_EyePupilRange.x + _EyePupilRange.y != 0,
				1,
				0
			);

			_EyePupil = IF(
				doEyePupil > 0,
				min(saturate(_EyePupilRange.x) + saturate(noise2) * saturate(_EyePupilRange.y), _EyePupilRange.y),
				_EyePupil
			);
			
			// Upper body color.
			fixed4 c = tex2D(_MainTex, uv) * _UpperColor;
			
			// Lower body color.
			c = IF(
				_LowerUnder > 0 && uv.y < _EdgeHeight.x,
				_LowerColor,
				c
			);

			fixed4 origc = c;
			float2 st = uv / _TilingAndOffset.xy;


			// Hat.
			c = IF(
				_HatUnder > 0 && uv.y > 1.0 - _EdgeHeight.y,
				_HatColor,
				c
			);


			fixed4 preMouthc = c;

			// Mouth part 1.
			fixed mouthc = circle(
				st + _Mouth.xy +
					float2(
						min(noise2b * saturate(_Mouth.z), _Mouth.z),
						min(noise2 * saturate(_Mouth.z), _Mouth.z)
					) * _Mouth.w,
				_Mouth.z
			);

			// Paint mouth.
			fixed doMouth = IF(
				mouthc > 0,
				1,
				0
			);

			c = IF(
				doMouth > 0,
				_MouthColor,
				c
			);

			// Mouth part 2 - occluder.
			fixed mouthc2 = circle(
				st + _Mouth.xy - _MouthShaper.xy + 
					float2(
						min(noise2 * saturate(_MouthShaper.z), _MouthShaper.z),
						min(noise2b * saturate(_MouthShaper.z), _MouthShaper.z)
					) * _MouthShaper.w,
				_Mouth.z * _MouthShaper.z
			);

			// Paint mouth.
			c = IF(
				doMouth > 0 && mouthc2 > 0,
				preMouthc,
				c
			);





			// Random blink.
			fixed doBlink = IF(
				random(float2(_Time.x, 7654.234 + _Seed)) < _EyeBlinkSpeed,
				1,
				0
			);

			st = IF(
				doBlink > 0,
				float2(0, 0),
				st
			);

			// Eye Y movement
			st += float2(_Scrolling.x, noise5 * noise2 + _Scrolling.y);
			_Shape.xy += float2(noise5, noise5) * noise5 * _EyePupilRange.z;

			// Random pupil move.
			fixed doPupilMove = 1;

			float pupilFactor = min(_Shape.x, _Shape.y) * saturate(_EyePupilRange.z);

			// Eye/pupil movement.
			fixed pupilX = IF(
				doPupilMove > 0,
				_SinTime.x * noise5 * pupilFactor,
				0
			);

			fixed pupilY = IF(
				doPupilMove > 0,
				_CosTime.w * noise5b * pupilFactor,
				0
			);

			fixed result1 = circle(st, _Shape.y);
			fixed result1p = circle(st + float2(pupilX, pupilY), _Shape.y * _EyePupil);
			fixed result2 = circle(st + _EyeDist.xy, _Shape.x);
			fixed result2p = circle(st + float2(pupilX, pupilY) + _EyeDist.xy, _Shape.x * _EyePupil);
			
			// Paint eye whites.
			fixed doWhites = IF(
				result1 + result2 > 0,
				1,
				0
			);

			c = IF(
				doWhites > 0,
				_EyeColorW,
				c
			);

			// Paint pupils.
			fixed doPupils = IF(
				result1p + result2p > 0,
				1,
				0
			);

			c = IF(
				doPupils > 0,
				_EyeColorP,
				c
			);


			// Lower body color.
			c = IF(
				_LowerUnder < 1 && uv.y < _EdgeHeight.x,
				_LowerColor,
				c
			);


			// Hat.
			c = IF(
				_HatUnder < 1 && uv.y > 1.0 - _EdgeHeight.y,
				_HatColor,
				c
			);

			o.Albedo = c.rgb;
			// Metallic and smoothness come from slider variables
			o.Metallic = _Metallic;
			o.Smoothness = _Glossiness;
			o.Alpha = c.a;
		}
		ENDCG
	}
	FallBack "Diffuse"
}
