THE DFAULTS
v1.0
Feb, 2020
Author: Andrew Ferguson, fergicide@gmail.com
Tested on and designed for: Unity 2018.4
Youtube: https://www.youtube.com/watch?v=4fMr-IwnMUI


INTRODUCTION

Inspired by this January 2020 Reddit post [https://www.reddit.com/r/Unity3D/comments/emseeq/i_was_tired_of_learning_with_a_faceless_capsule/] lamenting Unity's humdrum, faceless default capsule, I wondered what else might spruce up the oftentimes joyless exercise of early game prototyping.  What if the default capsule could have not just a face but an animated face?  I present to you: THE DFAULTS.

At first I imagined creating a set of templates for the Unity default capsule, the sphere, the cube and beyond.  I conked out after building the capsule templates.  Perhaps in the future I will flesh out the others.  Or perhaps you will be inspired to do it.  You now have all the required tools.  In the meantime, no Dcubes or Dspheres in this package, just Dcaps.

I am not a seasoned shader coder.  But I know enough now to get things working.  I set out to write an animation shader that adheres to these design goals:

#1 "Keep It Simple, Simon"
My name isn't Simon.  I figured this was the voice in my head talking to another voice in my head.

#2 Efficient
Handle a massive number of Dfaults on screen with ease.  That meant materialpropertyblock instancing.  Want to prototype an army of thousands rendered as default capsules?  An army comprising visually unique squads of soldier?  The Dfaults stand ready for deployment.  

#3 Elegant
Yeah, I wish.  I scratched this off while yelling "BIG NOPE!"  My shader-fu is still mostly fu-bar.  If you want elegant shader coding, go watch the amazing Art of Code [https://www.youtube.com/channel/UCcAlTqd9zID6aNX3TzwxJXg].  I will, however, comment the code as best I can to explain what the hell I'm trying to do. 

#4 Based on the default Unity surface shader
For simplicity and for having all the baked-in goodness without extra work.

Next, I contemplated how to create a "face."  Eyes, duh, tick.  Nose... nah, more decorative than expressive.  Mouth, definitely.  Ears, eyelids, eyebrows, etc. would be nice to have, but now Simpletown receeds in our rear-view while Whodoyouthinkiamartofcodeville looms ahead.  No, we'll make do with two eyes and a mouth, thank you.

What are eyes?  Black pupil set in white: two circles.  Easy!  I just need shader code that draws circles.  How about mouth?  A rectangle?  A bezier curve?  Steady now, I only just learned to draw shader circles!  Let not our shader reach exceed our shader grasp.  Crescents?  Oh, hey!  If you lay a circle atop another circle and slide it around, you create a crescent mouth-like shape.  Simplicity itself.  Just six circles to draw: a circle within a circle each for eyes, and two circles to form the mouth.

Beyond this simple manipulation of a few circles, all I needed now was color.  Controlling color via UV coordinates is super easy.  Not a problem.

And that's it: some circles, some color, some setup scripting and some basic shader code... and we've got THE DFAULTS.


PERFORMANCE

Running the "WeAreLegion" non-LOD scene in the editor, which creates 15,000 instanced Dcaps, I get these stats:

- 8 million verts
- 65 batches, 15K batches saved
- 3 set pass calls

The alternate LOD scene tops out at about 500K verts.


UNITY SETUP

This package contains prefabs to get you up and running, but you can ignore those and build your own Dfaults from scratch with just these three essential files: 

#1 The Dfaults shader: "DfaultsShader"
Add to your project and find it under "Fergicide/Dfaults" in the shader library.  Create a material from it.

#2 Scriptableobject: "DfaultsConfig"
For creating and storing Dfault configurations.  To start playing with new configurations, duplicate the existing Dcap configuration file under ASSETS > FERGICIDE > DFAULTS > CONFIGS > DCAPS, then add it to your DfaultsController component.  But to bootstrap quickly, why not just clone one of the example configs and go from there?

#3 Monobehaviour: "DfaultsController"
To be attached to every Dfault gameobject or prefab.  Requires two configuration properties: the Dfaults configuration file and the Dfaults material to use.  Create your Unity capsule gameobject, add the "DfaultsController" script to it, then drag a "DfaultsConfig" scriptableobject and the Dfaults material onto the controller script.  You are good to go.


DFAULTS CONFIGURATION SCRIPTABLEOBJECT

To create a brand new DfaultsConfig scriptableobject, in the Project window rightclick and choose CREATE > FERGICIDE > DFAULTS > NEW CONFIG.  You should probably be cloning one of the example DfaultConfigs and working up a new Dcap from that, but suit yourself.

The configuration isn't super easy or intuitive to use.  Sorry about that.  Changing property names and generally refactoring this script got trickier the further I got with the project.

In the inspector, hover your mouse over the properties to get an explanation.

If building a config from scratch, the first thing you want to do is set the eyes to be looking along the object's forward.  Once you have the "face" facing forward, you can start tweaking everything else.

My advice is to play with the settings and frequently hit CTRL+Z to undo in-editor property changes when things get crazy.  Clone the config scriptableobject whenever you feel you've made progress.  This makes for easy recovery if later changes work out poorly. 



EXTRAS

- LOD scene

The second version of the "WeAreLegion" example scene uses LODs.  That keeps the vert count down considerably.  Check that out if you plan on prototyping with ridiculous numbers of dfault capsules.

- Metal variants

You've got glossy and metal prefab variants to play with.


MISC NOTES

Early on, I contemplated adding instanced textures but chose not to.  Instancing textures requires Texture2DArrays.  That would add shader overhead and was not really in the spirit of keeping things simple.


END DOCO. 


 
