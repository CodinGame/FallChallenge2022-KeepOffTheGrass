<!-- LEAGUES level1 level2 level3 -->
<div id="statement_back" class="statement_back" style="display: none"></div>
<div class="statement-body">
	<!-- BEGIN level1 level2 -->
	<!-- LEAGUE ALERT -->
	<div style="color: #7cc576; 
	background-color: rgba(124, 197, 118,.1);
	padding: 20px;
	margin-right: 15px;
	margin-left: 15px;
	margin-bottom: 10px;
	text-align: left;">
		<div style="text-align: center; margin-bottom: 6px">
			<img src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png" />
  </div>
			<p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
				This is a <b>league based</b> challenge.
			</p>
			<div class="statement-league-alert-content">
				For this challenge, multiple leagues for the same game are available. Once you have proven yourself
				against the
				first Boss, you will access a higher league and harder opponents will be available.<br>
				<br>
			</div>

			</div>
			<!-- END -->

			<!-- GOAL -->
			<div class="statement-section statement-goal">
				<h2 style="font-size: 20px;">
					<span class="icon icon-goal">&nbsp;</span>
					<span>Goal</span>
				</h2>
				Control more <b>patches</b> than your opponent at the end of the match.
			</div>


			<!-- RULES -->
			<div class="statement-section statement-rules">
				<h2 style="font-size: 20px;">
					<span class="icon icon-rules">&nbsp;</span>
					<span>Rules</span>
				</h2>

				<div class="statement-rules-content">
					<div style="margin-bottom: 10px">
						<p><b>Robots</b> are deployed in a field of abandoned electronics, their purpose is to refurbish
							patches of this field into functional tech.</p>

						<p>The robots are also capable of self-disassembly and self-replication, but they need
							<b>raw materials</b> from structures called <b>Recyclers</b> which the robots can build.</p>

						<p>The structures will <b>recycle</b> everything around them into raw matter, essentially
							removing the patches of electronics and revealing the <b style="color: #6fb16a;">Grass</b>
							below.</p>

						Players control a <b>team</b> of these robots in the midst of a <b>playful competition</b> to
						see which team can control the most patches of a given scrap field. They do so by <b>marking</b>
						patches with their team's color, all with the following constraints:
						<ul>
							<li>If robots of both teams end up on the same patch, they must disassemble themselves
								<const>one
									for one</const>. The robots are therefore removed from the game, only leaving at
								most <const>one</const> team on that patch.</li>

							<li>The robots <b>may not cross the grass</b>, robots that are still on a patch when it is
								completely recycled must therefore disassemble themselves too.</li>
						</ul>
						<em>Once the games are over, the robots will dutifully re-assemble and go back to work as normal.</em>
					</div>

					<div style="display: flex; justify-content: center; align-items: center;">
						<div style="text-align: center; margin: 15px">
							<img src="/servlet/fileservlet?id=90169144893136"
          style="padding: 25px 0 15px 0; max-width: 100%" />
							<div><em>A blue-team robot.</em></div>
						</div>
						<div style="text-align: center; margin: 15px">
							<img src="/servlet/fileservlet?id=90169169211863"
          style="padding: 25px 0 15px 0; max-width: 100%" />
							<div><em>A red-team robot.</em></div>
						</div>
					</div>

					<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
						Map</h3>

					<p>The game is played on a grid of variable size.
						Each tile of the grid represents a patch of scrap electronics. The aim of the game is to control
						more
						tiles than your opponent, by having robots <b>mark</b> them.</p>

					Each tile has the following properties:
					<ul>
						<li><var>scrapAmount</var>: this patch's amount of usable scrap. It is equal to the amount of
							turns it will take to be completely recycled. If zero,
							this
							patch
							is <b style="color: #6fb16a;">Grass</b>.</li>
						<li><var>owner</var>: which player's team controls this patch. Will equal <const>-1</const> if
							the patch is
							neutral
							or
							<b style="color: #6fb16a;">Grass</b>.</li>
					</ul>


					<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
						Robots</h3>

					<p>Any number of robots can occupy a tile, but if units of opposing teams end the turn on the same
						tile,
						they are removed <const>1 for 1</const>. Afterwards, if the tile still has robots, they will
						mark
						that
						tile.</p>


					<div style="text-align: center; margin: 20px">
						<img src="/servlet/fileservlet?id=90169205333329"
					style="400px; max-width: 100%" />
						<div style="padding-top: 15px;">
							<em>After moving all robots to the middle tile, only one blue robot remains and the tile is marked.</em>
						</div>
					</div>

					Robots may not occupy a <b style="color: #6fb16a;">Grass</b> tile or share a tile with a
					<b>Recycler</b>.

					<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
						Recyclers</h3>

					<p>Recyclers are structures that take up a tile. Each turn, the tile below and all adjacent tiles are used for
						recycling,
						reducing their <var>scrapAmount</var> and providing <const>1</const> unit of matter to the
						recycler's
						owner.</p>
					If the tile under a recycler runs out of scrap, the recycler is dismantled.


					<div style="text-align: center; margin: 20px">
						<img src="/servlet/fileservlet?id=90169245033610"
					style="400px; max-width: 100%" />
						<div style="padding-top: 15px;">
							<em>Any tile within reach of your recyclers will grant <const>1 matter</const> per turn and their <var>scrapAmount</var> will decrease.</em>
						</div>
					</div>

					<p>A given tile can only be subject to recycling <b>once</b> per turn. Meaning its <var>scrapAmount</var>
						will go
						down
						by <const>1</const> even if a player has <b>multiple</b> adjacent Recyclers, providing that player with only <const>1</const> unit of matter. If a tile has adjacent Recyclers from <b>both</b> players, the same is true but both players will receive <const>1</const> unit of matter.
					</p>



					<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
						Matter</h3>

					<p>
						<const>10</const> units of matter can be spent to create a new robot, or to build another
						<b>Recycler</b>.
					</p>

					<p>At the end of each turn, both players receive an extra <const>10</const> matter.


						<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
							Actions</h3>
						<p>
							On each turn players can do any amount of valid actions, which include:
						</p>
						<ul>
							<li>
								<action>MOVE</action>: move a number of units from a tile to an adjacent tile. You may specify a non adjacent tile to move to, in which case the units will automatically select the best MOVE to approach the target.
							</li>
						</ul>
						<div style="text-align: center; margin: 20px">
							<img src="/servlet/fileservlet?id=90169189332531"
					style="width: 400px; max-width: 100%" />
							<div style="padding-top: 15px;"> <em> A </em>
								<action>MOVE</action> <em>to</em>
								<const>(3,0)</const><em> will result in this robot stepping into</em>
								<const>(1,2)</const>.</div>
						</div>
						<ul>
							<li>
								<action>BUILD</action>: erect a Recycler on the given empty tile the player controls.
							</li>
							</ul>
							<div style="text-align: center; margin: 20px">
        <img src="/servlet/fileservlet?id=90169127241165"
					style="width: 400px; max-width: 100%" />
      </div>
							<ul>
							<li>
								<action>SPAWN</action>: construct a number of robots on the given tile the player
								controls.
							</li>
						</ul>
													<div style="text-align: center; margin: 20px">
        <img src="/servlet/fileservlet?id=90169220174878"
					style="width: 400px; max-width: 100%" />
      </div>

						<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
							Action order for one turn</h3>


						<ol>
							<li>
								<action>BUILD</action> actions are computed.
							</li>
							<li>
								<action>MOVE</action> and <action>SPAWN</action> actions are computed simultaneously. A
								robot
								cannot do both on the same turn.
							</li>
							<li>
								Units of opposing teams on the same tile are removed one for one.
							</li>
							<li>
								Remaining robots will mark the tiles they are on, changing their <var>owner</var>.
							</li>
							<li>
								Recyclers affect the tiles they are on and the 4 adjacent tiles that are not Grass.
							</li>
							<li>Tiles with size <const>0</const> are now <b style="color: #6fb16a;">Grass</b>. Recyclers
								and robots on that tile
								are
								removed.
							</li>
							<li>The players receive <const>10</const> base matter as well as the matter from recycling.
							</li>
						</ol>

						<br>


						<!-- Victory conditions -->
						<div class="statement-victory-conditions">
							<div class="icon victory"></div>
							<div class="blk">
								<div class="title">Victory Conditions</div>
								<div class="text">
									<p style="padding-top:0; padding-bottom: 0;">
										The winner is the player who controls the most <b>tiles</b> after either:
										<ul>
											<li>A player no longer controls a single tile.</li>
											<li>
												<const>20</const> turns have passed without any tile changing
												<var>scrapAmount</var> or <var>owner</var>.
											</li>
											<li>
												<const>200</const> turns have been played.
											</li>
										</ul>
									</p>
								</div>
							</div>
						</div>
						<!-- Lose conditions -->
						<div class="statement-lose-conditions">
							<div class="icon lose"></div>
							<div class="blk">
								<div class="title">Defeat Conditions</div>
								<div class="text">
									<p style="padding-top:0; padding-bottom: 0;">
										Your program does not provide a command in the allotted time or it provides an
										unrecognized command.
									</p>
								</div>
							</div>
						</div>
						<br>
						<h3 style="font-size: 16px;
                font-weight: 700;
                padding-top: 20px;
        color: #838891;
                padding-bottom: 15px;">
							🐞 Debugging tips</h3>
						<ul>
							<li>Hover over a tile to see extra information about it, including it's
								<b>history</b>.</li>
							<li>Use the <action>MESSAGE</action> command to display some text on your side of the HUD.
							</li>
							<li>Press the gear icon on the viewer to access extra display options.</li>
							<li>Use the keyboard to control the action: space to play/pause, arrows to step 1 frame at a
								time.
							</li>
						</ul>

				</div>
			</div>

			<!-- EXPERT RULES -->

			<div class="statement-section statement-expertrules">
				<h2 style="font-size: 20px;">
					<span class="icon icon-expertrules">&nbsp;</span>
					<span>Technical Details</span>
				</h2>
				<div class="statement-expert-rules-content">
					<ul style="padding-left: 20px;padding-bottom: 0">
						<li>A tile's <var>owner</var> will not change if there are no robots on it at end of turn.</li>
						<li>If the target of a <action>MOVE</action> is unreachable, the robots will target the
							reachable tiles closest to the given destination, preferring the one closest to the center
							of the map.</li>
						<li>When selecting a path to <action>MOVE</action> to a distant tile, the robots will take the
							shortest route, preferring to stay near the center of the map when possible.</li>
						<li>
							<action>MOVE</action> and <action>SPAWN</action> happen simultaneously and cannot conflict
							with each other. However, they may be cancelled by a <action>BUILD</action> action, even if
							it comes later in the player's output, or is part of the opponent's actions.
						</li>
						<!--
						<li>
          You can check out the source code of this game <a rel="nofollow" target="_blank"
            href="https://github.com/CGjupoulton-utg2021/UTG2021">on this GitHub repo</a>.
			 -->
						</li>
					</ul>
				</div>
			</div>


			<!-- PROTOCOL -->
			<div class="statement-section statement-protocol">
				<h2 style="font-size: 20px;">
					<span class="icon icon-protocol">&nbsp;</span>
					<span>Game Protocol</span>
				</h2>

				<!-- Protocol block -->
				<div class="blk">
					<div class="title">Initialization Input</div>
					<span class="statement-lineno">One line:</span> two integers <var>width</var> and <var>height</var>
					for the size of the map. The top-left tile is <const>(x,y) = (0,0)</const>.<br>
				  </div>
					<div class="blk">
						<div class="title">Input for One Game Turn</div>
						<span class="statement-lineno">First line:</span> two integers <var>myMatter</var> and
						<var>oppMatter</var> for the amount of matter owned by each player.<br>
						<span class="statement-lineno">Next <var>height</var> lines:</span> one line per cell, starting
						at <const>(0,0)</const> and incrementing from left to right, top to bottom. Each cell is
						represented by <const>7</const> integers:<br>
						<br>The first <const>4</const> variables describe properties for this tile:
						<ul>
							<li><var>scrapAmount</var>: the number of times this tile can be recycled before becoming
								<b style="color: #6fb16a;">Grass</b>.</li>
							<li><var>owner</var>: <ul>
									<li>
										<const>1</const> if you control this cell.
									</li>
									<li>
										<const>0</const> if your opponent controls this cell.
									</li>
									<li>
										<const>-1</const> otherwise.
									</li>
								</ul>
							</li>
							<li><var>units</var>: the number of units on this cell. These units belong to the
								<var>owner</var> of the cell.</li>
							<li><var>recycler</var>:


								<const>1</const> if there is a recycler on this cell. This recycler belongs to
								the <var>owner</var> of the cell. <const>0</const> if there is no recycler on this cell.


							</li>
						</ul>
						<br>The next <const>3</const> variables are helper values:
						<ul>
							<li><var>canBuild</var>:

								<const>1</const> if you are allowed to <action>BUILD</action> a recycler on this
								tile this turn.

								<const>0</const> otherwise.

							</li>
							<li><var>canSpawn</var>:
								<const>1</const> if you are allowed to <action>SPAWN</action> units on this tile
								this turn.

								<const>0</const> otherwise.

							</li>
							<li><var>inRangeOfRecycler</var>:

								<const>1</const> if this tile's <var>scrapAmount</var> will be decreased at the end
								of the turn by a nearby recycler.

								<const>0</const> otherwise.

							</li>

						</ul>


						<div class="blk">
							<div class="title">Output</div>
							<div class="text">
								All your actions one one line, separated by a <action>;</action>
								<ul>
									<li>
										<action>MOVE</action> <var>amount</var> <var>fromX</var> <var>fromY</var>
										<var>toX</var>
										<var>toY</var>.
										Automatic
										pathfinding.
									</li>
									<li>
										<action>BUILD</action> <var>x</var> <var>y</var>. Builds a recycler.
									</li>
									<li>
										<action>SPAWN</action> <var>amount</var> <var>x</var> <var>y</var>. Adds unit to
										an
										owned tile.
									</li>
									<li>
										<action>WAIT</action>. Does nothing.
									</li>
									<li>
										<action>MESSAGE</action> <var>text</var>. Displays text on your side of the HUD.
									</li>
								</ul>
							</div>
						</div>

						<div class="blk">
							<div class="title">Constraints</div>
							<div class="text">
					<!-- BEGIN level1 -->
						<const>12</const> ≤ <var>width</var> ≤ <const>15</const><br>
						<const>6</const> ≤ <var>height</var> ≤ <const>7</const><br>
					<!-- END -->
					<!-- BEGIN level2 level3 -->
					  <const>12</const> ≤ <var>width</var> ≤ <const>24</const><br>
					  <const>6</const> ≤ <var>height</var> ≤ <const>12</const><br>
					<!-- END -->
								Response time per turn ≤ <const>50</const>ms<br>
								Response time for the first turn ≤ <const>1000</const>ms
							</div>
						</div>
					</div>
					<!-- BEGIN level1 -->
					<!-- LEAGUE ALERT -->
					<div style="color: #7cc576; 
                      background-color: rgba(124, 197, 118,.1);
                      padding: 20px;
                      margin-top: 10px;
                      text-align: left;">
						<div style="text-align: center; margin-bottom: 6px"><img
        src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png" /></div>

							<div style="text-align: center; font-weight: 700; margin-bottom: 6px;">
								What is in store for me in the higher leagues?
							</div>
							<ul>
								<li>Larger maps will be available.</li>
							</ul>
						</div>
						<!-- END -->


						<!-- STORY -->
						<div class="statement-story-background">
							<div class="statement-story-cover"
								style="background-size: cover; background-image: url(/servlet/fileservlet?id=89683207589973)">
								<div class="statement-story" style="min-height: 300px; position: relative">
									<h2><span style="color: #b3b9ad">Keep Off The Grass!</span></h2>
									<div class="story-text">
										<p>The life of a <b style="color: #f2bb13">Recyclo-Bot</b> is a simple one.</p>
										<p>Mark scrap for refurbishment, build recyclers, move on to the next field of
											scrap and repeat, all while respecting the Prime Directive:
											<b style="color: #f2bb13">"Keep Off The Grass"</b>. But sometimes, even the
											most cheerful little <b style="color: #f2bb13">Recyclo-Bot</b> can get a bit
											bored by these repetitive tasks. </p>
										<p>This is why, once in a while, the self-proclaimed
											<b style="color: #f2bb13">Recyclo-Boyz</b> like to organize the
											<b style="color: #f2bb13">Great Scrap Marking Competition</b>, a friendly
											joust between two teams where the one having marked the most scrap with
											their color at the end of a timer is declared the winner.
										</p>
										However, during a match the robots may only use raw materials recycled from the
										scrap field they are standing on! All tricks are allowed, even recycling to such
										an extent that the honoured <b style="color: #f2bb13">Grass</b> is uncovered,
										blocking off a patch of scrap from the opponent... or completely pulling the rug
										out from under oneself, if not careful enough.

<br><br><br>
<h2><span style="color: #b3b9ad"><b>Starter Kit</b></span></h2>
Starter AIs are available in the
      <a target="_blank" href="https://github.com/CodinGame/FallChallenge2022-KeepOffTheGrass/tree/main/starterAIs">Starter Kit</a>.
      They can help you get started with your own bot. You can modify them to suit your own coding style or start completely
          from scratch.

									</div>
								</div>
							</div>
						</div>
					</div>
					<!-- SHOW_SAVE_PDF_BUTTON -->