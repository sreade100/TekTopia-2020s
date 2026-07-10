How the code works:

function getActionBlock()
This will get the block location which the action can be performed and an ENUM to say if the environment exists, if the block exists, or if the block has no room for access.
Has the following input overloads:
- block: determines if there is a block of that type
- block,block_state: determines if there is a block of that type in that block_state (i.e. age)
- environment: will generate a random target which fits the environmental conditions (i.e. fence)
- environment,block: determines if there is a block of that type within the defined environment (i.e. fence)
- environment,block,block_state: determines if there is a block of that type in that block_state (i.e. age) within the defined environment (i.e. fence) 

## How the json works
*trigger* sets the task on the register such that:
- 0 = constantly added to the regsiter without any requests
- 1 = only added to the register when a request is made 
- 2 = constantly added to the register, but only done when requests are complete

## Custom Environment Requirements
The following are specially implemented environmental requirements;

FENCEBOUNDARY
- This will determine the internal area of a fence with no sign attached.

FENCEBOUNDARY,_____
- This will determine the internal area of a fence with a sign attached with the item ______ in that sign. i.e. a watermelon, wool

SUGARCANESTACK
- This will detect if sugar cane is stacked and where to break it

FINDANIMAL
- This will find an animal inside the rendered area

## Special Actions

WATERCROPS, _______(opt)
- Will determine distance between water in fence boundary and will dig water holes for them

LEADANIMAL
- Will generate two actions. It will fetch the environmental pose to deposit an animal and the position of the animal to lead
