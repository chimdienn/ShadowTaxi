This README document outlines the assumptions made during the implementation of the game.

1. Passengers

Priority: Passengers priority will change dynamically based on the rain and umbrella conditions, both outside and inside
the taxi. Whenever the priority changes, the current earnings are recalculated accordingly.

Ejection from Taxi: When ejected, the passenger will follow the driver's movement without overlapping with the driver.

Trip Completion: When passengers are walking to their destination flags, they still receive damage if they collide with
other entities. If their health drops below 0, it results in a loss.

2. Driver

Entering a New Taxi: When getting into a new taxi, if there is already a passenger in the trip, the driver must wait for
the passenger to get in. The passenger's movement is the same as when they walk to their destination flag. During
time, even though the driver is already in the taxi, the taxi cannot move until the current in-trip passenger gets in.

3. Taxi

When colliding with other entities, if the taxi is above that entity, the taxi is pushed up by 10 frames.
There is a possibility that the taxi will be pushed up multiple times and eventually move out of the top edge of the
screen. This leads to a game loss.

When a new taxi is generated and the driver has not got in, that taxi can still receive and inflict damage to other entities.

4. Cars

When timed out, other cars and enemy cars cannot move for the entire timeout duration.

If cars collide with other entities that are in timeout, that car cannot move for the same duration (called the
"Standing" phase) but can still receive and inflict damage if it collides with any other entity.

5. Collision Mechanics

Collision Animations: All collisions between any two entities will always implement the "pushed up" or "pushed down" animation.

Timeout and Damage: If any entity is in a timeout duration, neither of the two entities will take damage. The timeout
duration is set if and only if an entity takes damage, meaning the timeout duration cannot be renewed.

Smoke Effect: Smoke is rendered if and only if the taxi or cars take damage and their health is still above 0.

Invincibility: When the taxi or driver is invincible, their timeout duration is cleared, and they cannot take damage
by any means. However, the taxi can still inflict damage on other entities.

