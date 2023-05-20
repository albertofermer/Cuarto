Shader "Fergicide/SmoothMovement"
{
	Properties
	{
		_Color ("Color", Color) = (1,1,1,1)
		[hdr] _EColor("Emission Color", Color) = (1,1,1,1)
		_MainTex("Albedo (RGB)", 2D) = "white" {}
		_Glossiness ("Smoothness", Range(0,1)) = 0.5
		_Metallic ("Metallic", Range(0,1)) = 0.0
		_Cutoff("Alpha Cutoff Value", Range(0,1)) = 0.5
		[Header(X seed   Y amp   Z freq   W speed)]
		_X ("X-axis params", Vector) = (0.45678, 0.2385, 2.0, 0.1)
		_Y ("Y-axis params", Vector) = (0.342, 0.084, 4.0, 0.1)
		_Z ("Z-axis params", Vector) = (0.2402, 0.1865, 2.0, 0.1)
	}
	SubShader
	{
		Tags { "RenderType"="Opaque" }
		LOD 200

		CGPROGRAM
		// Physically based Standard lighting model, and enable shadows on all light types
		#pragma surface surf Standard fullforwardshadows vertex:vert alphatest:_Cutoff

		// Use shader model 3.0 target, to get nicer looking lighting
		#pragma target 3.0

		sampler2D _MainTex;
		half4 _X, _Y, _Z;

		struct Input
		{
			float2 uv_MainTex;
		};

		half _Glossiness;
		half _Metallic;
		fixed4 _Color, _EColor;

		// Add instancing support for this shader. You need to check 'Enable Instancing' on materials that use the shader.
		// See https://docs.unity3d.com/Manual/GPUInstancing.html for more information about instancing.
		// #pragma instancing_options assumeuniformscaling
		UNITY_INSTANCING_BUFFER_START(Props)
			// put more per-instance properties here
		UNITY_INSTANCING_BUFFER_END(Props)

		// Seed must be 0-1 float.
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

		void vert (inout appdata_full v) {
			v.vertex.xyz += float3(
				afnoise(_X.x, _X.y, _X.z, _X.w),
				afnoise(_Y.x, _Y.y, _Y.z, _Y.w),
				afnoise(_Z.x, _Z.y, _Z.z, _Z.w)
			);
		}

		void surf (Input IN, inout SurfaceOutputStandard o)
		{
			// Albedo comes from a texture tinted by color
			fixed4 c = tex2D (_MainTex, IN.uv_MainTex) * _Color;
			o.Albedo = c.rgb;
			// Metallic and smoothness come from slider variables
			o.Metallic = _Metallic;
			o.Smoothness = _Glossiness;
			o.Alpha = c.a;
			o.Emission = _EColor;
		}
		ENDCG
	}
	FallBack "Diffuse"
}
