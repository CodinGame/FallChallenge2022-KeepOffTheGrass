<!-- LEAGUES level1 level2 -->
<div id="statement_back" class="statement_back" style="display: none"></div>
<div class="statement-body">
	<!-- BEGIN level1 -->
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
			Ce challenge est basé sur un système de <b>leagues</b>.
			</p>
			<div class="statement-league-alert-content">
				Pour ce challenge, plusieurs ligues pour le même jeu seront disponibles. Quand vous aurez prouvé votre valeur
      contre le premier Boss, vous accéderez à la ligue supérieure et débloquerez de nouveaux adversaires.
				<br>
				<br>
			</div>

			</div>
			<!-- END -->

			<!-- GOAL -->
			<div class="statement-section statement-goal">
				<h2 style="font-size: 20px;">
					<span class="icon icon-goal">&nbsp;</span>
					<span>Objectif</span>
				</h2>
				Contrôler plus de  <b>cases</b> que votre adversaire à la fin du match.
			</div>


			<!-- RULES -->
			<div class="statement-section statement-rules">
				<h2 style="font-size: 20px;">
					<span class="icon icon-rules">&nbsp;</span>
					<span>Règles</span>
				</h2>

				<div class="statement-rules-content">
					<div style="margin-bottom: 10px">
						<p>Les <b>Robots</b> sont déployés dans un champ de débris électroniques abandonnées, leur fonction est de reconditionner des parcelles de s pour en faire de nouveaux objets.</p>

						<p>Les robots sont aussi capables de se désassembler et de se répliquer, mais ils ont besoin de <b>ressources brutes</b> provenant de bâtiments appelés <b>recycleurs</b> que peuvent construire les robots.</p>

						<p>Les bâtiments vont <b>recycler</b> toutes les cases autour d'eux en ressources brutes, pour faire simple ils convertissent les parcelles de débris en parcelles d'<b style="color: #6fb16a;">herbe</b>, qui se trouvent en dessous.</p>

						<p>Les joueurs contrôlent une <b>équipe</b> de ces robots au cours d'une <b>compétition amicale</b>, pour déterminer quelle équipe peut contrôler le plus de parcelles dans un champ de débris donné. Les équipes <b>marquent</b> des cases avec la couleur de leur équipe, avec les contraintes suivantes:
						<ul>
							<li>Si des robots des deux équipes se retrouvent sur la même case, ils doivent se désassembler eux-même <const>un pour un</const>. Les robots sont donc enlevés du jeu, laissant ainsi au plus une équipe sur la case.</li>

							<li>Les robots <b>ne peuvent pas traverser l'herbe</b>, ceux qui sont encore sur une case quand elle est entièrement recyclée doivent donc eux-aussi se désassembler.</li>
						</ul>
						<em>Une fois la partie terminée, les robots se réassemblereont et retourneront travailler comme si de rien n'était.</em>
					</div>

					<div style="display: flex; justify-content: center; align-items: center;">
						<div style="text-align: center; margin: 15px">
							<img src="/servlet/fileservlet?id=90169144893136"
          style="padding: 25px 0 15px 0; max-width: 100%" />
							<div><em>Un robot de l'équipe bleue.</em></div>
						</div>
						<div style="text-align: center; margin: 15px">
							<img src="/servlet/fileservlet?id=90169169211863"
          style="padding: 25px 0 15px 0; max-width: 100%" />
							<div><em>Un robot de l'équipe rouge.</em></div>
						</div>
					</div>

					<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
						La carte</h3>

					<p>La partie est jouée sur une grille de taille variable.
						Chaque case sur la grille représente une parcelle de débris électroniques. Le but du jeu est de contrôler plus de cases que son adversaire, en les faisant <b>marquer</b> par des robots.
						Chaque case a les propriétés suivantes:
					<ul>
						<li><var>scrapAmount</var> : la quantité de matériau utilisable sur la case. Elle est égale au nombre de tours nécessaires pour être complêtement recyclée. Si égale a 0, cette case est de l'<b style="color: #6fb16a;">herbe</b>.</li>
						<li><var>owner</var> : quel joueur contrôle cette case. Vaut <const>-1</const> si la case est neutre ou est de l'<b style="color: #6fb16a;">herbe</b>
					</ul>


					<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
						Les robots</h3>

					<p>N'importe quel nombre de robots peut occuper une case, mais si des unités des deux équipes finissent leur tour sur la même case, ils sont retirés du jeu <const>1 pour 1</const>. Après cela, si la case possède encore des robots, ces derniers marqueront la case.</p>

					<div style="text-align: center; margin: 20px">
						<img src="/servlet/fileservlet?id=90169205333329"
					style="400px; max-width: 100%" />
						<div style="padding-top: 15px;">
							<em>Après avoir bougé tous les robots sur la case du milieu, seul un robot bleu y reste et la tuile est marquée.</em>
						</div>
					</div>

					Les robots ne peuvent pas occuper une case d'<b style="color: #6fb16a;">herbe</b> ou partager une case avec un <b>recycleur</b>

					<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
						Les recycleurs</h3>

					<p>Les recycleurs sont des bâtiments qui occupent une case. Chaque tour, sa case et toutes les cases adjacentes sont utilisées pour être recyclées, réduisant ainsi leur <var>scrapAmount</var> et rapportant <const>1</const> unité de matériaux au propriétaire du recycleur.</p>

					Si la case sur laquelle est posée un recycleur est à court de <var>scrapAmount</var>, le recycleur est démonté.

					<div style="text-align: center; margin: 20px">
						<img src="/servlet/fileservlet?id=90169245033610"
					style="400px; max-width: 100%" />
						<div style="padding-top: 15px;">
							<em>Toute tuile à la portée de vos recycleurs vous rapporte <const>1 matériau</const> par tour et perd <const>1</const> <var>scrapAmount</var>.</em>
						</div>
					</div>

					<p>Une tuile donnée ne peut être recyclée qu'une seule fois par tour. Cela implique que son <var>scrapAmount</var> sera réduit de <const>1</const> même si les deux joueurs ont plusieurs recycleurs adjacents, leur rapportant chacun uniquement <const>1 matériau</const>.
					</p>



					<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
						Le Materiau</h3>

					<p>
						<const>10</const> matériaux peuvent être dépensées pour créer un nouveau robot, ou pour construire un autre <b>recycleur</b>.
					</p>

					<p>A la fin de chaque tour, les deux joueurs reçoivent <const>10</const> matériaux en bonus.

						<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
							Les actions</h3>
						<p>
							À chaque tour les joueurs peuvent entreprendre n'importe quel nombre d'actions valides, qui sont:
						</p>
						<ul>
							<li>
								<action>MOVE</action> : déplace un nombre donné d'unités d'une case à une case adjacente. Si la case ciblée n'est pas adjacente, les unités avanceront sur le chemin le plus court jusqu'à la destination.
							</li>
						</ul>
						<div style="text-align: center; margin: 20px">
							<img src="/servlet/fileservlet?id=90169189332531"
					style="width: 400px; max-width: 100%" />
							<div style="padding-top: 15px;"> <em> Un </em>
								<action>MOVE</action> <em>vers</em>
								<const>(3,0)</const><em> fera marcher ce robots vers</em>
								<const>(1,2)</const>.</div>
						</div>
						<ul>
							<li>
								<action>BUILD</action> : construit un recycleur sur une case vide spécifiée que le joueur contrôle.
							</li>
							</ul>
							<div style="text-align: center; margin: 20px">
        <img src="/servlet/fileservlet?id=90169127241165"
					style="width: 400px; max-width: 100%" />
      </div>
							<ul>
							<li>
								<action>SPAWN</action> : construit un nombre donné de robots sur la case spécifiée que le joueur contrôle.
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
							L'ordre des actions sur un tour</h3>


						<ol>
							<li>
								Les actions <action>BUILD</action> sont complétées.
							</li>
							<li>
								Les actions <action>MOVE</action> et <action>SPAWN</action> sont simultanément complétées. Un même robot ne peut faire ces deux actions pendant le même tour
							</li>
							<li>
								Les unités des équipes opposées sur une case sont retirées <const>un pour un</const>.
							</li>
							<li>
								Les robots restants marqueront les cases sur lesquelles elles se trouvent, changeant le <var>owner</var> de la case.
							</li>
							<li>
								Les recycleurs recyclent la case sur lesquelles elles se trouvent et les 4 cases adjacentes tant qu'elles ne sont pas déjà de l'<b style="color: #6fb16a;">herbe</b>
							</li>
							<li>Les tuiles avec un <var>scrapAmount</var> à <const>0</const> sont maintenant de l'<b style="color: #6fb16a;">herbe</b>. Les recycleurs ou les robots sur cette case sont retirés.
							</li>
							<li>Les joueurs reçoivent de base <const>10</const> matériaux ainsi que ceux acquis lors du recyclage.
							</li>
						</ol>
						<br>

						<!-- Victory conditions -->
						<div class="statement-victory-conditions">
							<div class="icon victory"></div>
							<div class="blk">
								<div class="title">Conditions de Victoire</div>
								<div class="text">
									<p style="padding-top:0; padding-bottom: 0;">
										Le gagnant est celui qui contrôle le plus de <b>cases</b> après soit:
										<ul>
											<li>Un joueur ne contrôle plus une seule case.</li>
											<li>
												<const>20</const> tours se sont passés sans qu'une seule case n'ait changé de <var>scrapAmount</var> ou d'<var>owner</var>.
											</li>
											<li>
												<const>200</const> tours ont été joués.
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
								<div class="title">Conditions de Défaite</div>
								<div class="text">
									<p style="padding-top:0; padding-bottom: 0;">
										Votre programme ne répond pas dans le temps imparti ou il fournit une commande non reconnue.
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
							🐞 Conseils de débogage</h3>
						<ul>
							<li>Survolez une case pour voir plus d'informations sur celle-ci, y compris son <b>historique</b>.</li>
							<li>Utilisez la commande <action>MESSAGE</action> pour afficher du texte sur votre côté du HUD.
							</li>
<li>Cliquez sur la roue dentée pour afficher les options visuels supplémentaires</li>
      <li>Utilisez le clavier pour contrôler l'action : espace pour play / pause, les flèches pour avancer pas à pas
						</ul>

				</div>
			</div>

			<!-- EXPERT RULES -->

			<div class="statement-section statement-expertrules">
				<h2 style="font-size: 20px;">
					<span class="icon icon-expertrules">&nbsp;</span>
					<span>Détails Techniques</span>
				</h2>
				<div class="statement-expert-rules-content">
					<ul style="padding-left: 20px;padding-bottom: 0">
						<li>L'<var>owner</var> d'une case ne changera pas s'il y n'y a pas de robots dessus à la fin du tour.</li>
						<li>Si la cible d'une commande <action>MOVE</action> n'est <b>pas atteignable</b>, les robots iront sur la case la plus proche de la destination donnée, préférant celle la plus proche du centre de la carte.</li>
						<li>En choisissant un chemin pour la commande <action>MOVE</action> dont la cible est <b>éloignée</b> mais atteignable, les robots prendront le plus court chemin, préférant rester proche du centre de la carte si possible.</li>
						<li>
							Les commandes <action>MOVE</action> et <action>SPAWN</action> se produisent simultanément et ne peuvent pas entrer en conflit l'une avec l'autre. Cependant elles peuvent être invalidées par une commande <action>BUILD</action>, même si elle arrive plus tard dans la liste de commandes du joueur, ou fait partie des actions de l'adversaire.
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
					<span>Protocole de Jeu</span>
				</h2>

				<!-- Protocol block -->
				<div class="blk">
					<div class="title">Entrées d'Initialisation</div>
					<span class="statement-lineno">Une ligne: </span> deux entiers <var>width</var> et <var>height</var> pour la largeur et la hauteur de la carte. La case en haut à gauche est <const>(x,y) = (0,0)</const>.<br>
				  </div>
					<div class="blk">
						<div class="title">Entrées pour Un Tour de Jeu</div>
						<span class="statement-lineno">Première ligne:</span> deux entiers <var>myMatter</var> et
						<var>oppMatter</var> pour la quantité de matériaux de chaque joueur.<br>
						<span class="statement-lineno">Les <var>height</var> * <var>width</var> lignes suivantes:</span> une ligne par case, commençant par celle en <const>(0,0)</const> et allant de gauche à droite, de haut en bas. Chaque case est représentée par <const>7</const> entiers:<br>
						<br>Les <const>4</const> premières variables décrivent les propriétés pour cette case&nbsp;:
						<ul>
							<li><var>scrapAmount</var>: le nombre de fois que cette case peut être recyclée avant de devenir de l'<b style="color: #6fb16a;">herbe</b>.</li>
							<li><var>owner</var>: <ul>
									<li>
										<const>1</const> si vous contrôlez cette case.
									</li>
									<li>
										<const>0</const> si votre adversaire contrôle cette case.
									</li>
									<li>
										<const>-1</const> sinon.
									</li>
								</ul>
							</li>
							<li><var>units</var>: le nombre d'unités sur cette case. Ces unités appartiennent à l'<var>owner</var> de la case.</li>
							<li><var>recycler</var>:


								<const>1</const> si il y a un recycleur sur cette case. Ce recycleur appartient à l'<var>owner</var> de cette case. <const>0</const> s'il n'y pas pas de recycleur sur cette case.
							</li>
						</ul>
						<br>Les <const>3</const> variables suivantes sont là pour vous aider:
						<ul>
							<li><var>canBuild</var>:

								<const>1</const> si vous avez le droit de <action>BUILD</action> (construire) un recycleur sur cette case ce tour.

								<const>0</const> sinon.

							</li>
							<li><var>canSpawn</var>:
								<const>1</const> si vous avez le droit de <action>SPAWN</action> (ajouter) des robots sur cette case ce tour.
								<const>0</const> sinon.

							</li>
							<li><var>inRangeOfRecycler</var>:

								<const>1</const> si cette tuile se fera réduire sa <var>scrapAmount</var> par un recycleur à la fin du tour.

								<const>0</const> sinon.

							</li>

						</ul>


						<div class="blk">
							<div class="title">Sortie</div>
							<div class="text">
								Toutes vos actions sur une ligne, séparées par un <action>;</action>
								<ul>
									<li>
										<action>MOVE</action> <var>amount</var> <var>fromX</var> <var>fromY</var>
										<var>toX</var>
										<var>toY</var>.
										Pathfinding automatique.
									</li>
									<li>
										<action>BUILD</action> <var>x</var> <var>y</var>. Construit un recycleur sur une case libre contrôlée.
									</li>
									<li>
										<action>SPAWN</action> <var>amount</var> <var>x</var> <var>y</var>.  Ajoute des robots sur une case contrôlée.
									</li>
									<li>
										<action>WAIT</action>. Ne fait rien.
									</li>
									<li>
										<action>MESSAGE</action> <var>text</var>. Affiche un texte sur votre côté du HUD.
									</li>
								</ul>
							</div>
						</div>

						<div class="blk">
							<div class="title">Contraintes</div>
							<div class="text">
					<!-- BEGIN level1 -->
						<const>12</const> ≤ <var>width</var> ≤ <const>15</const><br>
						<const>6</const> ≤ <var>height</var> ≤ <const>7</const><br>
					<!-- END -->
					<!-- BEGIN level2 -->
					  <const>12</const> ≤ <var>width</var> ≤ <const>24</const><br>
					  <const>6</const> ≤ <var>height</var> ≤ <const>12</const><br>
					<!-- END -->
								Temps de réponse par tour ≤ <const>50</const>ms<br>
								Temps de réponse pour le premier tour ≤ <const>1000</const>ms
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
								Qu'est-ce qui m'attend dans les ligues suivantes ?
							</div>
							<ul>
								<li>Des cartes plus grandes seront disponibles.</li>
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
										<p>La vie d'un <b style="color: #f2bb13">Recycloro-bot</b> est simple.</p>
										<p>Noter l'emplacement de la ferraille, construire des recycleurs, aller dans le prochain champ de débris et répétez, tout en respectant la Première Loi:
											<b style="color: #f2bb13">"Ne Roulez Pas sur l'Herbe"</b>. Mais parfois, même le plus joyeux des <b style="color: #f2bb13">Recycloro-bot</b> peut être un peu lassé par ces tâches répétitives. </p>
										<p>C'est pourquoi, de temps en temps, ils aiment organiser
											<b style="color: #f2bb13">La Grande Ferraillation</b>, une joute amicale entre deux équipes où celle qui marque le plus de débris avec sa couleur à la fin du temps imparti est déclarée vainqueur.
										</p>
										Cependant, pendant un match les robots ne peuvent utiliser que les ressources recyclées du tas de feraille sur lequel ils se trouvent&nbsp;! Tous les coups sont permis, même recycler à un tel point que la précieuse <b style="color: #f2bb13">herbe</b> est déblayée, coupant l'accès à votre adversaire... ou coupant la branche sur laquelle vous êtes assis, si vous ne faites pas attention...
										<br><br>
										<br>
						<h2><span style="color: #b3b9ad"><b>Kit de Démarrage</b></span></h2>
								Des IAs de base sont disponibles dans le <a target="_blank"
        href="https://github.com/CodinGame/FallChallenge2022-KeepOffTheGrass/tree/main/starterAIs">kit de démarrage</a>. Elles peuvent
      vous aider à appréhender votre propre IA.
<br><br><br>
<h2><span style="color: #b3b9ad"><b>Code Dource</b></span></h2>
Le code source de ce jeu est disponible <a target="_blank" href="https://github.com/CodinGame/FallChallenge2022-KeepOffTheGrass">ici</a>.
									</div>
								</div>
							</div>
						</div>
					</div>
					<!-- SHOW_SAVE_PDF_BUTTON -->