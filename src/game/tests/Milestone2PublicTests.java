package game.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;


public class Milestone2PublicTests {

	private String gamePath="game.engine.Game";
	private String boardPath="game.engine.Board";
	private String constantsPath="game.engine.Constants";
	private String rolePath="game.engine.Role";

	private String cardPath="game.engine.cards.Card";
	private String swapperCardPath="game.engine.cards.SwapperCard";
	private String startOverCardPath="game.engine.cards.StartOverCard";
	private String shieldCardPath="game.engine.cards.ShieldCard";
	private String energyStealCardPath="game.engine.cards.EnergyStealCard";
	private String confusionCardPath="game.engine.cards.ConfusionCard";

	private String cellPath="game.engine.cells.Cell";
	private String contaminationSockPath="game.engine.cells.ContaminationSock";
	private String cardCellPath="game.engine.cells.CardCell";
	private String conveyorBeltPath="game.engine.cells.ConveyorBelt";
	private String doorCellPath="game.engine.cells.DoorCell";
	private String monsterCellPath="game.engine.cells.MonsterCell";
	private String transportCellPath="game.engine.cells.TransportCell";

	private String dataLoaderPath="game.engine.dataloader.DataLoader";

	private String gameActionExceptionPath="game.engine.exceptions.GameActionException";
	private String invalidCSVFormatPath="game.engine.exceptions.InvalidCSVFormat";
	private String invalidMoveExceptionPath="game.engine.exceptions.InvalidMoveException";
	private String invalidTurnExceptionPath="game.engine.exceptions.InvalidTurnException";
	private String OutOfEnergyExceptionPath="game.engine.exceptions.OutOfEnergyException";

	private String dasherPath="game.engine.monsters.Dasher";
	private String dynamoPath="game.engine.monsters.Dynamo";
	private String monsterPath="game.engine.monsters.Monster";
	private String multiTaskerPath="game.engine.monsters.MultiTasker";
	private String schemerPath="game.engine.monsters.Schemer";

	private String canisterModifierPath="game.engine.interfaces.CanisterModifier";



	private static ArrayList<String> cards_csv;
	private static ArrayList<String> cells_csv;
	private static ArrayList<String> monsters_csv;

	@Test(timeout = 1000)
	public void testSetCardsByRarityIsPrivate() {
		try {
			Class<?> boardClass = Class.forName(boardPath);

			Method m = boardClass.getDeclaredMethod("setCardsByRarity");
			int modifiers = m.getModifiers();

			assertTrue("setCardsByRarity in Board should be private", Modifier.isPrivate(modifiers));
		} catch (Exception e) {
			fail("Unexpected exception in testSetCardsByRarityIsPrivate: " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testAlterEnergyIsFinal() {
		try {
			Class<?> monsertClass = Class.forName(monsterPath);

			Method m = monsertClass.getDeclaredMethod("alterEnergy",int.class);
			int modifiers = m.getModifiers();

			assertTrue("alterEnergy in Monsetr should be final", Modifier.isFinal(modifiers));
		} catch (Exception e) {
			fail("Unexpected exception in testAlterEnergyIsFinal: " + e.getMessage());
		}
	}


	@Test(timeout = 1000)
	public void testReloadCardsModifiers() {
		try {
			Class<?> boardClass = Class.forName(boardPath);

			Method m = boardClass.getDeclaredMethod("reloadCards");
			int modifiers = m.getModifiers();

			assertTrue("reloadCards in Board should be public", Modifier.isPublic(modifiers));
			assertTrue("reloadCards in Board should be static", Modifier.isStatic(modifiers));
		} catch (Exception e) {
			fail("Unexpected exception in testReloadCardsModifiers: " + e.getMessage());
		}
	}



	@Test(timeout = 2000)
	public void testBoardConstructorInitializesBoardCellsAndStationedMonsters() {
		try {
			Class<?> constantsClass = Class.forName(constantsPath);
			Field rowsField = constantsClass.getDeclaredField("BOARD_ROWS");
			Field colsField = constantsClass.getDeclaredField("BOARD_COLS");
			int rows = rowsField.getInt(null);
			int cols = colsField.getInt(null);

			ArrayList<Object> readCards = new ArrayList<Object>();

			Class<?> boardClass = Class.forName(boardPath);
			Constructor<?> boardCtor = boardClass.getConstructor(ArrayList.class);
			Object boardObj = boardCtor.newInstance(readCards);

			Field boardCellsField = boardClass.getDeclaredField("boardCells");
			boardCellsField.setAccessible(true);
			Object[][] boardCells = (Object[][]) boardCellsField.get(boardObj);

			assertNotNull("Board constructor should initialize boardCells array", boardCells);
			assertEquals("boardCells row count should equal Constants.BOARD_ROWS", rows, boardCells.length);
			for (int i = 0; i < rows; i++) {
				assertNotNull("Each row in boardCells should be initialized", boardCells[i]);
				assertEquals("Each row in boardCells should have length Constants.BOARD_COLS",
						cols, boardCells[i].length);
			}

			Field stationedField = boardClass.getDeclaredField("stationedMonsters");
			stationedField.setAccessible(true);
			@SuppressWarnings("unchecked")
			ArrayList<Object> stationed = (ArrayList<Object>) stationedField.get(null);

			assertNotNull("Board constructor should initialize stationedMonsters list", stationed);
			assertTrue("stationedMonsters should be empty upon Board construction", stationed.isEmpty());
		} catch (Exception e) {
			fail("Unexpected exception in testBoardConstructorInitializesBoardCellsAndStationedMonsters: "
					+ e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}

	@Test(timeout = 2000)
	public void testBoardConstructorExpandsOriginalCardsAndInitializesActiveDeck() {
		try {
			Class<?> boardClass = Class.forName(boardPath);
			Field originalField = boardClass.getDeclaredField("originalCards");
			originalField.setAccessible(true);
			@SuppressWarnings("unchecked")
			ArrayList<Object> originalBackup = (ArrayList<Object>) originalField.get(null);

			Method getCardsMethod = boardClass.getMethod("getCards");
			@SuppressWarnings("unchecked")
			ArrayList<Object> cardsBackup = (ArrayList<Object>) getCardsMethod.invoke(null);

			Class<?> swapperClass = Class.forName(swapperCardPath);
			Constructor<?> swapperCtor = swapperClass.getConstructor(String.class, String.class, int.class);
			Object lowRarityCard = swapperCtor.newInstance("Low", "Rarity 1", Integer.valueOf(1));

			Class<?> shieldClass = Class.forName(shieldCardPath);
			Constructor<?> shieldCtor = shieldClass.getConstructor(String.class, String.class, int.class);
			Object highRarityCard = shieldCtor.newInstance("High", "Rarity 3", Integer.valueOf(3));

			ArrayList<Object> readCards = new ArrayList<Object>();
			readCards.add(lowRarityCard);
			readCards.add(highRarityCard);

			Constructor<?> boardCtor = boardClass.getConstructor(ArrayList.class);
			boardCtor.newInstance(readCards);

			@SuppressWarnings("unchecked")
			ArrayList<Object> expandedOriginal = (ArrayList<Object>) originalField.get(null);

			@SuppressWarnings("unchecked")
			ArrayList<Object> activeDeck = (ArrayList<Object>) getCardsMethod.invoke(null);

			assertNotNull("Board constructor should initialize originalCards", expandedOriginal);
			assertEquals("originalCards size after constructor should match sum of rarities",
					4, expandedOriginal.size());

			int lowCount = 0;
			int highCount = 0;
			for (Object c : expandedOriginal) {
				if (c == lowRarityCard)
					lowCount++;
				if (c == highRarityCard)
					highCount++;
			}

			assertEquals("Low rarity card (rarity 1) should appear once in originalCards", 1, lowCount);
			assertEquals("High rarity card (rarity 3) should appear three times in originalCards", 3, highCount);

			assertNotNull("Board constructor should initialize the active cards deck", activeDeck);
			assertEquals("Active deck size after constructor should equal originalCards size",
					expandedOriginal.size(), activeDeck.size());

			Map<Object, Integer> expectedCounts = new LinkedHashMap<>();
			for (Object c : expandedOriginal)
				expectedCounts.put(c, expectedCounts.getOrDefault(c, 0) + 1);

			Map<Object, Integer> actualCounts = new LinkedHashMap<>();
			for (Object c : activeDeck)
				actualCounts.put(c, actualCounts.getOrDefault(c, 0) + 1);

			assertEquals("Active deck should contain the same multiset of card instances as originalCards",
					expectedCounts, actualCounts);

			originalField.set(null, originalBackup);
			if (cardsBackup != null) {
				Method setCardsMethod = boardClass.getMethod("setCards", ArrayList.class);
				setCardsMethod.invoke(null, cardsBackup);
			}
		} catch (Exception e) {
			fail("Unexpected exception in testBoardConstructorExpandsOriginalCardsAndInitializesActiveDeck: "
					+ e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}


	@Test(timeout = 2000)
	public void testReloadCardsUsesOriginalCardsAndFillsActiveDeck() {
		try {
			Class<?> boardClass = Class.forName(boardPath);
			Field originalField = boardClass.getDeclaredField("originalCards");
			originalField.setAccessible(true);
			@SuppressWarnings("unchecked")
			ArrayList<Object> originalBackup = (ArrayList<Object>) originalField.get(null);

			Method getCardsMethod = boardClass.getMethod("getCards");
			@SuppressWarnings("unchecked")
			ArrayList<Object> cardsBackup = (ArrayList<Object>) getCardsMethod.invoke(null);

			writeCardsCSVForDataLoader();
			Class<?> dataLoaderClass = Class.forName(dataLoaderPath);
			Method readCardsMethod = dataLoaderClass.getMethod("readCards");
			@SuppressWarnings("unchecked")
			ArrayList<Object> loadedCards = (ArrayList<Object>) readCardsMethod.invoke(null);

			originalField.set(null, loadedCards);

			Method setCardsMethod = boardClass.getMethod("setCards", ArrayList.class);
			setCardsMethod.invoke(null, new ArrayList<Object>());

			Method reloadCardsMethod = boardClass.getMethod("reloadCards");
			reloadCardsMethod.invoke(null);

			@SuppressWarnings("unchecked")
			ArrayList<Object> active = (ArrayList<Object>) getCardsMethod.invoke(null);

			assertNotNull("After reloadCards, active deck should not be null", active);
			assertEquals("reloadCards should copy all cards from originalCards into the active deck",
					loadedCards.size(), active.size());

			Map<Object, Integer> expectedCounts = new LinkedHashMap<>();
			for (Object c : loadedCards) {
				expectedCounts.put(c, expectedCounts.getOrDefault(c, 0) + 1);
			}

			Map<Object, Integer> actualCounts = new LinkedHashMap<>();
			for (Object c : active) {
				actualCounts.put(c, actualCounts.getOrDefault(c, 0) + 1);
			}

			assertEquals("Active deck after reloadCards should contain the same multiset of card instances as "
					+ "originalCards", expectedCounts, actualCounts);

			originalField.set(null, originalBackup);
			if (cardsBackup != null) {
				setCardsMethod.invoke(null, cardsBackup);
			}
		} catch (Exception e) {
			fail("Unexpected exception in testReloadCardsUsesOriginalCardsAndFillsActiveDeck: "
					+ e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}

	@Test(timeout = 2000)
	public void testDrawCardRemovesAndReturnsFirstCard() {
		try {
			Class<?> swapperClass = Class.forName(swapperCardPath);
			Constructor<?> swapperCtor = swapperClass.getConstructor(String.class, String.class, int.class);
			Object first = swapperCtor.newInstance(
					"First Swapper", "First card in deck", Integer.valueOf(1));

			Class<?> shieldClass = Class.forName(shieldCardPath);
			Constructor<?> shieldCtor = shieldClass.getConstructor(String.class, String.class, int.class);
			Object second = shieldCtor.newInstance(
					"Second Shield", "Second card in deck", Integer.valueOf(1));

			ArrayList<Object> deck = new ArrayList<Object>();
			deck.add(first);
			deck.add(second);

			Class<?> boardClass = Class.forName(boardPath);
			Method setCardsMethod = boardClass.getMethod("setCards", ArrayList.class);
			setCardsMethod.invoke(null, deck);

			Method drawCardMethod = boardClass.getMethod("drawCard");
			Object drawn = drawCardMethod.invoke(null);

			assertSame("drawCard should return the first card in the deck", first, drawn);
			assertEquals("drawCard should remove exactly one card from the deck", 1, deck.size());
			assertSame("After drawing, the remaining card should be the second one", second, deck.get(0));
		} catch (Exception e) {
			fail("Unexpected exception in testDrawCardRemovesAndReturnsFirstCard: "
					+ e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}

	@Test(timeout = 2000)
	public void testDrawCardReloadsWhenDeckIsEmpty() {
		try {
			Class<?> energyClass = Class.forName(energyStealCardPath);
			Constructor<?> energyCtor =
					energyClass.getConstructor(String.class, String.class, int.class, int.class);
			Object only = energyCtor.newInstance(
					"Only Energy", "Only card in original", Integer.valueOf(1), Integer.valueOf(50));

			ArrayList<Object> original = new ArrayList<Object>();
			original.add(only);

			Class<?> boardClass = Class.forName(boardPath);
			Field originalField = boardClass.getDeclaredField("originalCards");
			originalField.setAccessible(true);

			@SuppressWarnings("unchecked")
			ArrayList<Object> backupOriginal = (ArrayList<Object>) originalField.get(null);

			Method getCardsMethod = boardClass.getMethod("getCards");
			@SuppressWarnings("unchecked")
			ArrayList<Object> backupCards = (ArrayList<Object>) getCardsMethod.invoke(null);

			originalField.set(null, original);

			Method setCardsMethod = boardClass.getMethod("setCards", ArrayList.class);
			setCardsMethod.invoke(null, new ArrayList<Object>());

			Method drawCardMethod = boardClass.getMethod("drawCard");
			Object drawn = drawCardMethod.invoke(null);

			assertNotNull("drawCard should return a card even if deck was initially empty", drawn);
			assertSame("After reloading, drawCard should return the card from originalCards", only, drawn);

			@SuppressWarnings("unchecked")
			ArrayList<Object> afterDraw = (ArrayList<Object>) getCardsMethod.invoke(null);
			assertEquals("Active deck should be empty after drawing the only card", 0, afterDraw.size());

			originalField.set(null, backupOriginal);
			if (backupCards != null) {
				setCardsMethod.invoke(null, backupCards);
			}
		} catch (Exception e) {
			fail("Unexpected exception in testDrawCardReloadsWhenDeckIsEmpty: "
					+ e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}


	@Test(timeout = 2000)
	public void testSetCardsByRarityDuplicatesCardsAccordingToRarity() {
		try {
			Class<?> swapperClass = Class.forName(swapperCardPath);
			Constructor<?> swapperCtor = swapperClass.getConstructor(String.class, String.class, int.class);
			Object cardLowRarity = swapperCtor.newInstance("Low", "Low rarity", Integer.valueOf(1));

			Class<?> shieldClass = Class.forName(shieldCardPath);
			Constructor<?> shieldCtor = shieldClass.getConstructor(String.class, String.class, int.class);
			Object cardHighRarity =
					shieldCtor.newInstance("High", "High rarity", Integer.valueOf(3));

			ArrayList<Object> original = new ArrayList<Object>();
			original.add(cardLowRarity);
			original.add(cardHighRarity);

			Class<?> boardClass = Class.forName(boardPath);
			Constructor<?> boardCtor = boardClass.getConstructor(ArrayList.class);
			Object boardObj = boardCtor.newInstance(new ArrayList<Object>());

			Field originalField = boardClass.getDeclaredField("originalCards");
			originalField.setAccessible(true);

			@SuppressWarnings("unchecked")
			ArrayList<Object> backupOriginal = (ArrayList<Object>) originalField.get(null);

			originalField.set(null, original);

			Method setCardsByRarity = boardClass.getDeclaredMethod("setCardsByRarity");
			setCardsByRarity.setAccessible(true);
			setCardsByRarity.invoke(boardObj);

			@SuppressWarnings("unchecked")
			ArrayList<Object> expanded = (ArrayList<Object>) originalField.get(null);

			assertEquals("setCardsByRarity should expand originalCards according to rarity values",
					4, expanded.size());

			int lowCount = 0;
			int highCount = 0;
			for (Object c : expanded) {
				if (c == cardLowRarity)
					lowCount++;
				if (c == cardHighRarity)
					highCount++;
			}

			assertEquals("Card with rarity 1 should appear once in expanded list", 1, lowCount);
			assertEquals("Card with rarity 3 should appear three times in expanded list", 3, highCount);

			originalField.set(null, backupOriginal);
		} catch (Exception e) {
			fail("Unexpected exception in testSetCardsByRarityDuplicatesCardsAccordingToRarity: "
					+ e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}


	@Test(timeout = 2000)
	public void testCellOnLandSetsLandingMonsterAndMarksOccupied() {
		try {
			Class<?> cellClass = Class.forName(cellPath);
			Constructor<?> cellCtor = cellClass.getConstructor(String.class);
			Object cell = cellCtor.newInstance("Normal Cell");

			Class<?> roleClass = Class.forName(rolePath);
			Enum<?> scarerRole = Enum.valueOf((Class<Enum>) roleClass, "SCARER");
			Enum<?> laugherRole = Enum.valueOf((Class<Enum>) roleClass, "LAUGHER");

			Class<?> dasherClass = Class.forName(dasherPath);
			Constructor<?> dasherCtor =
					dasherClass.getConstructor(String.class, String.class, roleClass, int.class);

			Object landingMonster = dasherCtor.newInstance(
					"Landing", "Landing monster", scarerRole, Integer.valueOf(100));
			Object opponentMonster = dasherCtor.newInstance(
					"Opponent", "Opponent monster", laugherRole, Integer.valueOf(80));

			Class<?> monsterClass = Class.forName(monsterPath);
			Method onLand = cellClass.getMethod("onLand", monsterClass, monsterClass);
			onLand.invoke(cell, landingMonster, opponentMonster);

			Field monsterField = cellClass.getDeclaredField("monster");
			monsterField.setAccessible(true);
			Object storedMonster = monsterField.get(cell);

			assertSame("Cell.onLand should set its monster field to the landing monster",
					landingMonster, storedMonster);

			Method isOccupied = cellClass.getMethod("isOccupied");
			boolean occupied = (boolean) isOccupied.invoke(cell);
			assertTrue("Cell.onLand should mark the cell as occupied", occupied);
		} catch (Exception e) {
			fail("Unexpected exception in testCellOnLandSetsLandingMonsterAndMarksOccupied: "
					+ e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}


	@Test(timeout = 2000)
	public void testCardCellOnLandDrawsCardAndAppliesEffect() {
		try {
			Class<?> energyClass = Class.forName(energyStealCardPath);
			Constructor<?> energyCtor =
					energyClass.getConstructor(String.class, String.class, int.class, int.class);
			Object stealCard = energyCtor.newInstance(
					"Steal 50", "Test steal card", Integer.valueOf(1), Integer.valueOf(50));

			ArrayList<Object> deck = new ArrayList<Object>();
			deck.add(stealCard);

			Class<?> boardClass = Class.forName(boardPath);
			Method setCardsMethod = boardClass.getMethod("setCards", ArrayList.class);
			setCardsMethod.invoke(null, deck);

			Method getCardsMethod = boardClass.getMethod("getCards");

			Class<?> roleClass = Class.forName(rolePath);
			Enum<?> scarerRole = Enum.valueOf((Class<Enum>) roleClass, "SCARER");
			Enum<?> laugherRole = Enum.valueOf((Class<Enum>) roleClass, "LAUGHER");

			Class<?> dasherClass = Class.forName(dasherPath);
			Constructor<?> dasherCtor =
					dasherClass.getConstructor(String.class, String.class, roleClass, int.class);

			Object landingMonster = dasherCtor.newInstance(
					"Landing", "Landing monster", scarerRole, Integer.valueOf(0));
			Object opponentMonster = dasherCtor.newInstance(
					"Opponent", "Opponent monster", laugherRole, Integer.valueOf(200));

			Class<?> cardCellClass = Class.forName(cardCellPath);
			Constructor<?> cardCellCtor = cardCellClass.getConstructor(String.class);
			Object cardCell = cardCellCtor.newInstance("Card Cell");

			Class<?> monsterClass = Class.forName(monsterPath);
			Method onLand = cardCellClass.getMethod("onLand", monsterClass, monsterClass);
			onLand.invoke(cardCell, landingMonster, opponentMonster);

			@SuppressWarnings("unchecked")
			ArrayList<Object> afterDeck = (ArrayList<Object>) getCardsMethod.invoke(null);
			assertEquals("CardCell.onLand should draw exactly one card from the deck",
					0, afterDeck.size());

			Method getEnergy = monsterClass.getMethod("getEnergy");
			int landingEnergy = (int) getEnergy.invoke(landingMonster);
			int opponentEnergy = (int) getEnergy.invoke(opponentMonster);

			assertEquals("Landing monster should gain 50 energy from EnergyStealCard", 50, landingEnergy);
			assertEquals("Opponent monster should lose 50 energy from EnergyStealCard", 150, opponentEnergy);
		} catch (Exception e) {
			fail("Unexpected exception in testCardCellOnLandDrawsCardAndAppliesEffect: "
					+ e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}

	@Test(timeout = 2000)
	public void testGameConstructorInitializesCoreFields() {
		try {
			Class<?> roleClass = Class.forName(rolePath);

			Enum<?> scarerRole = Enum.valueOf((Class<Enum>) roleClass, "SCARER");

			Class<?> gameClass = Class.forName(gamePath);
			Constructor<?> gameConstructor = gameClass.getConstructor(roleClass);
			Object gameObject = gameConstructor.newInstance(scarerRole);

			Method getBoard = gameClass.getMethod("getBoard");
			Method getAllMonsters = gameClass.getMethod("getAllMonsters");
			Method getPlayer = gameClass.getMethod("getPlayer");
			Method getOpponent = gameClass.getMethod("getOpponent");
			Method getCurrent = gameClass.getMethod("getCurrent");

			Object board = getBoard.invoke(gameObject);
			Object allMonsters = getAllMonsters.invoke(gameObject);
			Object player = getPlayer.invoke(gameObject);
			Object opponent = getOpponent.invoke(gameObject);
			Object current = getCurrent.invoke(gameObject);

			assertNotNull("Board should be initialized in Game constructor", board);
			assertNotNull("allMonsters should be initialized in Game constructor", allMonsters);
			assertNotNull("Player should be initialized in Game constructor", player);
			assertNotNull("Opponent should be initialized in Game constructor", opponent);
			assertNotNull("Current monster should be initialized in Game constructor", current);

			assertSame("Current monster should initially be the player", player, current);
		} catch (Exception e) {
			fail("Unexpected exception in testGameConstructorInitializesCoreFields: " + e.getMessage());
		}
	}

	@Test(timeout = 2000)
	public void testGameConstructorRespectsPlayerRoleAndOpponentRole() {
		try {
			Class<?> roleClass = Class.forName(rolePath);

			Enum<?> scarerRole = Enum.valueOf((Class<Enum>) roleClass, "SCARER");

			Class<?> gameClass = Class.forName(gamePath);
			Constructor<?> gameConstructor = gameClass.getConstructor(roleClass);
			Object gameObject = gameConstructor.newInstance(scarerRole);

			Method getPlayer = gameClass.getMethod("getPlayer");
			Method getOpponent = gameClass.getMethod("getOpponent");

			Object player = getPlayer.invoke(gameObject);
			Object opponent = getOpponent.invoke(gameObject);

			Method getRole = Class.forName(monsterPath).getMethod("getRole");

			Object playerRole = getRole.invoke(player);
			Object opponentRole = getRole.invoke(opponent);

			assertEquals("Player's role should match constructor argument", scarerRole, playerRole);
			assertNotEquals("Opponent's role should be opposite to player's role", scarerRole, opponentRole);
		} catch (Exception e) {
			fail("Unexpected exception in testGameConstructorRespectsPlayerRoleAndOpponentRole: " + e.getMessage());
		}
	}

	@Test(timeout = 2000)
	public void testGameConstructorRemovesPlayerAndOpponentFromStationedMonsters() {
		try {
			Class<?> roleClass = Class.forName(rolePath);

			Enum<?> laugherRole = Enum.valueOf((Class<Enum>) roleClass, "LAUGHER");

			Class<?> gameClass = Class.forName(gamePath);
			Constructor<?> gameConstructor = gameClass.getConstructor(roleClass);
			Object gameObject = gameConstructor.newInstance(laugherRole);

			Method getPlayer = gameClass.getMethod("getPlayer");
			Method getOpponent = gameClass.getMethod("getOpponent");
			Method getAllMonsters = gameClass.getMethod("getAllMonsters");

			Object player = getPlayer.invoke(gameObject);
			Object opponent = getOpponent.invoke(gameObject);

			ArrayList<Object> stationed = (ArrayList<Object>) getAllMonsters.invoke(gameObject);

			assertFalse("Player should not be among stationed monsters", stationed.contains(player));
			assertFalse("Opponent should not be among stationed monsters", stationed.contains(opponent));
		} catch (Exception e) {
			fail("Unexpected exception in testGameConstructorRemovesPlayerAndOpponentFromStationedMonsters: "
					+ e.getMessage());
		}
	}


	@Test(timeout = 1000)
	public void testSelectRandomMonsterByRoleIsPrivate() {
		try {
			Class<?> gameClass = Class.forName(gamePath);
			Class<?> roleClass = Class.forName(rolePath);

			Method m = gameClass.getDeclaredMethod("selectRandomMonsterByRole", roleClass);
			int modifiers = m.getModifiers();

			assertTrue("selectRandomMonsterByRole in Game should be private", Modifier.isPrivate(modifiers));
		} catch (Exception e) {
			fail("Unexpected exception in testSelectRandomMonsterByRoleIsPrivate: " + e.getMessage());
		}
	}

	@Test(timeout = 2000)
	public void testSelectRandomMonsterByRoleReturnsMonsterWithRequestedRole() {
		try {
			Class<?> roleClass = Class.forName(rolePath);

			Enum<?> scarerRole = Enum.valueOf((Class<Enum>) roleClass, "SCARER");

			Class<?> gameClass = Class.forName(gamePath);
			Constructor<?> gameConstructor = gameClass.getConstructor(roleClass);
			Object gameObject = gameConstructor.newInstance(scarerRole);

			Method selectMethod = gameClass.getDeclaredMethod("selectRandomMonsterByRole", roleClass);
			selectMethod.setAccessible(true);

			Object selected = selectMethod.invoke(gameObject, scarerRole);

			assertNotNull("selectRandomMonsterByRole should return a monster when such role exists", selected);

			Method getRole = Class.forName(monsterPath).getMethod("getRole");
			Object selectedRole = getRole.invoke(selected);

			assertEquals("Selected monster should have the requested role", scarerRole, selectedRole);
		} catch (Exception e) {
			fail("Unexpected exception in testSelectRandomMonsterByRoleReturnsMonsterWithRequestedRole: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 2000)
	public void testSelectRandomMonsterByRoleReturnsNullWhenNoMatchingRole() {
		try {
			Class<?> roleClass = Class.forName(rolePath);

			Enum<?> scarerRole = Enum.valueOf((Class<Enum>) roleClass, "SCARER");

			Enum<?> laugherRole = Enum.valueOf((Class<Enum>) roleClass, "LAUGHER");

			Class<?> gameClass = Class.forName(gamePath);
			Constructor<?> gameConstructor = gameClass.getConstructor(roleClass);
			Object gameObject = gameConstructor.newInstance(scarerRole);

			Field allMonstersField = gameClass.getDeclaredField("allMonsters");
			allMonstersField.setAccessible(true);

			ArrayList<Object> originalList = (ArrayList<Object>) allMonstersField.get(gameObject);

			ArrayList<Object> onlyLaughters = new ArrayList<>();
			if (originalList != null) {
				Method getRole = Class.forName(monsterPath).getMethod("getRole");
				for (Object m : originalList) {
					Object r = getRole.invoke(m);
					if (r.equals(laugherRole)) {
						onlyLaughters.add(m);
					}
				}
			}
			allMonstersField.set(gameObject, onlyLaughters);

			Method selectMethod = gameClass.getDeclaredMethod("selectRandomMonsterByRole", roleClass);
			selectMethod.setAccessible(true);

			Object selected = selectMethod.invoke(gameObject, scarerRole);

			assertNull("selectRandomMonsterByRole should return null when no monster has the requested role",
					selected);
		} catch (Exception e) {
			fail("Unexpected exception in testSelectRandomMonsterByRoleReturnsNullWhenNoMatchingRole: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 2000)
	public void testPlayTurnSkipsWhenCurrentIsFrozenAndUnfreezes() {
		try {
			Class<?> roleClass = Class.forName(rolePath);

			Enum<?> scarerRole = Enum.valueOf((Class<Enum>) roleClass, "SCARER");

			Class<?> gameClass = Class.forName(gamePath);
			Constructor<?> gameConstructor = gameClass.getConstructor(roleClass);
			Object gameObject = gameConstructor.newInstance(scarerRole);

			Method getCurrent = gameClass.getMethod("getCurrent");
			Method getOpponent = gameClass.getMethod("getOpponent");
			Object currentBefore = getCurrent.invoke(gameObject);
			Object opponentBefore = getOpponent.invoke(gameObject);

			Class<?> monsterClass = Class.forName(monsterPath);
			Method setFrozen = monsterClass.getMethod("setFrozen", boolean.class);
			Method isFrozen = monsterClass.getMethod("isFrozen");

			setFrozen.invoke(currentBefore, true);

			Method playTurn = gameClass.getMethod("playTurn");
			playTurn.invoke(gameObject);

			boolean frozenAfter = (boolean) isFrozen.invoke(currentBefore);
			Object currentAfter = getCurrent.invoke(gameObject);

			assertFalse("Frozen monster should be unfrozen after its skipped turn", frozenAfter);
			assertSame("Turn should switch to the opponent after skipping a frozen monster", opponentBefore,
					currentAfter);
		} catch (Exception e) {
			fail("Unexpected exception in testPlayTurnSkipsWhenCurrentIsFrozenAndUnfreezes: " + e.getClass()
					.getSimpleName() + " - " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testRollDiceIsPrivate() {
		try {
			Class<?> gameClass = Class.forName(gamePath);
			Method m = gameClass.getDeclaredMethod("rollDice");
			int modifiers = m.getModifiers();
			assertTrue("rollDice in Game should be private", Modifier.isPrivate(modifiers));
		} catch (Exception e) {
			fail("Unexpected exception in testRollDiceIsPrivate: " + e.getClass().getSimpleName() + " - "
					+ e.getMessage());
		}
	}

	@Test(timeout = 2000)
	public void testRollDiceReturnsValueBetweenOneAndSix() {
		try {
			Class<?> roleClass = Class.forName(rolePath);

			Enum<?> scarerRole = Enum.valueOf((Class<Enum>) roleClass, "SCARER");

			Class<?> gameClass = Class.forName(gamePath);
			Constructor<?> gameConstructor = gameClass.getConstructor(roleClass);
			Object gameObject = gameConstructor.newInstance(scarerRole);

			Method rollDice = gameClass.getDeclaredMethod("rollDice");
			rollDice.setAccessible(true);

			for (int i = 0; i < 20; i++) {
				int roll = (int) rollDice.invoke(gameObject);
				assertTrue("rollDice should return a value >= 1", roll >= 1);
				assertTrue("rollDice should return a value <= 6", roll <= 6);
			}
		} catch (Exception e) {
			fail("Unexpected exception in testRollDiceReturnsValueBetweenOneAndSix: " + e.getClass().getSimpleName()
					+ " - " + e.getMessage());
		}
	}


	@Test(timeout = 1000)
	public void testSwitchTurnIsPrivate() {
		try {
			Class<?> gameClass = Class.forName(gamePath);
			Method m = gameClass.getDeclaredMethod("switchTurn");
			int modifiers = m.getModifiers();
			assertTrue("switchTurn in Game should be private", Modifier.isPrivate(modifiers));
		} catch (Exception e) {
			fail("Unexpected exception in testSwitchTurnIsPrivate: " + e.getClass().getSimpleName() + " - "
					+ e.getMessage());
		}
	}

	@Test(timeout = 2000)
	public void testSwitchTurnTogglesBetweenPlayerAndOpponent() {
		try {
			Class<?> roleClass = Class.forName(rolePath);

			Enum<?> scarerRole = Enum.valueOf((Class<Enum>) roleClass, "SCARER");

			Class<?> gameClass = Class.forName(gamePath);
			Constructor<?> gameConstructor = gameClass.getConstructor(roleClass);
			Object gameObject = gameConstructor.newInstance(scarerRole);

			Method getPlayer = gameClass.getMethod("getPlayer");
			Method getOpponent = gameClass.getMethod("getOpponent");
			Method getCurrent = gameClass.getMethod("getCurrent");

			Object player = getPlayer.invoke(gameObject);
			Object opponent = getOpponent.invoke(gameObject);

			Method switchTurn = gameClass.getDeclaredMethod("switchTurn");
			switchTurn.setAccessible(true);

			Object current = getCurrent.invoke(gameObject);
			assertSame("Initial current should be player", player, current);

			switchTurn.invoke(gameObject);
			current = getCurrent.invoke(gameObject);
			assertSame("After one switchTurn call, current should be opponent", opponent, current);

			switchTurn.invoke(gameObject);
			current = getCurrent.invoke(gameObject);
			assertSame("After two switchTurn calls, current should be player again", player, current);
		} catch (Exception e) {
			fail("Unexpected exception in testSwitchTurnTogglesBetweenPlayerAndOpponent: "
					+ e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testGetCurrentOpponentIsPrivate() {
		try {
			Class<?> gameClass = Class.forName(gamePath);
			Method m = gameClass.getDeclaredMethod("getCurrentOpponent");
			int modifiers = m.getModifiers();
			assertTrue("getCurrentOpponent in Game should be private", Modifier.isPrivate(modifiers));
		} catch (Exception e) {
			fail("Unexpected exception in testGetCurrentOpponentIsPrivate: " + e.getClass().getSimpleName() + " - "
					+ e.getMessage());
		}
	}

	@Test(timeout = 2000)
	public void testGetCurrentOpponentReturnsOpponentRelativeToCurrent() {
		try {
			Class<?> roleClass = Class.forName(rolePath);

			Enum<?> scarerRole = Enum.valueOf((Class<Enum>) roleClass, "SCARER");

			Class<?> gameClass = Class.forName(gamePath);
			Constructor<?> gameConstructor = gameClass.getConstructor(roleClass);
			Object gameObject = gameConstructor.newInstance(scarerRole);

			Method getPlayer = gameClass.getMethod("getPlayer");
			Method getOpponent = gameClass.getMethod("getOpponent");
			Method getCurrent = gameClass.getMethod("getCurrent");

			Object player = getPlayer.invoke(gameObject);
			Object opponent = getOpponent.invoke(gameObject);

			Method getCurrentOpponent = gameClass.getDeclaredMethod("getCurrentOpponent");
			getCurrentOpponent.setAccessible(true);

			Object current = getCurrent.invoke(gameObject);
			assertSame("Initial current should be player", player, current);

			Object returnedOpponent = getCurrentOpponent.invoke(gameObject);
			assertSame("When current is player, getCurrentOpponent should return opponent", opponent, returnedOpponent);

			gameClass.getMethod("setCurrent", Class.forName(monsterPath)).invoke(gameObject, opponent);
			current = getCurrent.invoke(gameObject);
			assertSame("After setting current to opponent, current should be opponent", opponent, current);

			returnedOpponent = getCurrentOpponent.invoke(gameObject);
			assertSame("When current is opponent, getCurrentOpponent should return player", player, returnedOpponent);
		} catch (Exception e) {
			fail("Unexpected exception in testGetCurrentOpponentReturnsOpponentRelativeToCurrent: "
					+ e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}


	@Test(timeout = 1000)
	public void testAlterEnergyExistsWithCorrectParameter() {
		try {

			Class monsterClass = Class.forName(monsterPath);
			Method alterEnergyMethod = monsterClass.getDeclaredMethod("alterEnergy", int.class);
			assertEquals("alterEnergy should be a void method", void.class, alterEnergyMethod.getReturnType());
			assertTrue("alterEnergy method should be a public one.",Modifier.isPublic(alterEnergyMethod.getModifiers()));

		} catch(ClassNotFoundException e){

			fail("The monster class not found");

		} catch (NoSuchMethodException e) {

			fail("The monster class does not contain alterEnergy method which takes energy as a parameter");

		} catch (SecurityException e) {

			 fail(e.getClass() + " " + e.getMessage());

		}
	}

	@Test(timeout = 1000)
	public void testAlterEnergyWithShieldAndNegativeEnergy() {

		Object dasherMonster = null;
		try {
			dasherMonster = createDasher();
		} catch (Exception e) {
			 fail(e.getClass() + " " + e.getMessage());
		}

		 Field shieldedField = null;

		try {

		    shieldedField = dasherMonster.getClass().getSuperclass().getDeclaredField("shielded");
		    shieldedField.setAccessible(true);

		    Method  setShielded = dasherMonster.getClass().getSuperclass().getDeclaredMethod("setShielded", boolean.class);
		    setShielded.invoke(dasherMonster, true);

		}
		catch (NoSuchFieldException e) {
			fail("The monster class does not have shielded attribute");
		}
		catch (NoSuchMethodException e) {
			fail("The monster class does not have a setShielded method");
		}
		catch (InvocationTargetException e) {
        	fail("The setShielded method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		catch (IllegalAccessException e) {
			 fail(e.getClass() + " " + e.getMessage());
		}



		Field energyField = null;

		try {
			 energyField = dasherMonster.getClass().getSuperclass().getDeclaredField("energy");
			 energyField.setAccessible(true);
			 int energyValueBeforeInvoking = energyField.getInt(dasherMonster);

			 int negativeEnergy = (new Random().nextInt(10) + 1) * -1;

			 Method alterEnergyMethod = dasherMonster.getClass().getSuperclass().getDeclaredMethod("alterEnergy", int.class);
			 alterEnergyMethod.invoke(dasherMonster, negativeEnergy);
			 assertFalse("The monster should lose its shield if it receives a damage",shieldedField.getBoolean(dasherMonster));
			 assertEquals("The monster's energy should not be effected when it is shielded",energyValueBeforeInvoking ,energyField.getInt(dasherMonster));

		} catch (NoSuchFieldException e) {
			fail("The monster class does not have energy attribute");
		}
		catch(NoSuchMethodException e) {
			fail("The monster class does not contain alterEnergy method which takes energy as a parameter");
		}
		catch (InvocationTargetException e) {
			fail("The alterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		 catch (IllegalAccessException e) {
			 fail(e.getClass() + " " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testAlterEnergyWithoutShieldAndNegativeEnergy() {

		Object dynamoMonster = null;
		try {
			dynamoMonster = createDynamo();
		} catch (Exception e) {
			fail(e.getCause() + " occured while creating a new dynamo monster");
		}

		 Field shieldedField = null;

		try {

		    shieldedField = dynamoMonster.getClass().getSuperclass().getDeclaredField("shielded");
		    shieldedField.setAccessible(true);

		}
		catch (NoSuchFieldException e) {
			fail("The monster class does not have shielded attribute");
		}


		Field energyField = null;

		try {
			 energyField = dynamoMonster.getClass().getSuperclass().getDeclaredField("energy");
			 energyField.setAccessible(true);
			 int energyValueBeforeInvoking = energyField.getInt(dynamoMonster);

			 int negativeEnergy = (new Random().nextInt(10) + 1) * -1;

			 int energyValueAfterInvoking = energyValueBeforeInvoking + negativeEnergy*2;
			 if(energyValueAfterInvoking < 0)
				 energyValueAfterInvoking = 0;

			 Method alterEnergyMethod = dynamoMonster.getClass().getSuperclass().getDeclaredMethod("alterEnergy", int.class);
			 alterEnergyMethod.invoke(dynamoMonster, negativeEnergy);
			 assertFalse("The monster's shield should still be deactivated ",shieldedField.getBoolean(dynamoMonster));
			 assertEquals("The monster should receive a damage that alters its energy when it is not shielded",energyValueAfterInvoking ,energyField.getInt(dynamoMonster));

		} catch (NoSuchFieldException e) {
			fail("The monster class does not have energy attribute");
		}
		catch(NoSuchMethodException e) {
			fail("The monster class does not contain alterEnergy method which takes energy as a parameter");
		}
		catch (InvocationTargetException e) {
			fail("The alterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		 catch (IllegalAccessException e) {
			 fail(e.getClass() + " " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testAlterEnergyWithShieldAndPositiveEnergy() {

		Object multiTaskerMonster = null;
		try {
			multiTaskerMonster = createMultiTasker();
		} catch (Exception e) {
			fail(e.getCause() + " occured while creating a new dasher monster");
		}

		 Field shieldedField = null;

		try {

		    shieldedField = multiTaskerMonster.getClass().getSuperclass().getDeclaredField("shielded");
		    shieldedField.setAccessible(true);

		    Method  setShielded = multiTaskerMonster.getClass().getSuperclass().getDeclaredMethod("setShielded", boolean.class);
		    setShielded.invoke(multiTaskerMonster, true);

		}
		catch (NoSuchFieldException e) {
			fail("The monster class does not have shielded attribute");
		}
		catch (NoSuchMethodException e) {
			fail("The monster class does not have a setShielded method");
		}
		catch (InvocationTargetException e) {
        	fail("The setShielded method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		catch (IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
			}



		Field energyField = null;
		 int energyValueBeforeInvoking = 0;
		 int positiveEnergy = 0;

		try {
			 energyField = multiTaskerMonster.getClass().getSuperclass().getDeclaredField("energy");
			 energyField.setAccessible(true);
			 energyValueBeforeInvoking = energyField.getInt(multiTaskerMonster);

			 positiveEnergy = new Random().nextInt(20) + 1;

			 Method alterEnergyMethod = multiTaskerMonster.getClass().getSuperclass().getDeclaredMethod("alterEnergy", int.class);
			 alterEnergyMethod.invoke(multiTaskerMonster, positiveEnergy);
			 assertTrue("The monster should not lose its shield if it receives a healing energy",shieldedField.getBoolean(multiTaskerMonster));


		} catch (NoSuchFieldException e) {
			fail("The monster class does not have energy attribute");
		}
		catch(NoSuchMethodException e) {
			fail("The monster class does not contain alterEnergy method which takes energy as a parameter");
		}
		catch (InvocationTargetException e) {
			fail("The alterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		 catch (IllegalAccessException e) {
			 fail(e.getClass() + " " + e.getMessage());
		}

		try {

			Field MULTITASKER_BONUS_FIELD = Class.forName(constantsPath).getDeclaredField("MULTITASKER_BONUS");
			int energyValueAfterInvoking = energyValueBeforeInvoking + positiveEnergy + MULTITASKER_BONUS_FIELD.getInt(null);

			assertEquals("The monster's energy should be increased when it receives a healing energy",energyValueAfterInvoking ,energyField.getInt(multiTaskerMonster));

		} catch (NoSuchFieldException e) {
			fail("The constant class should contains a MULTITASKER_BONUS variable");
		} catch (IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (ClassNotFoundException e) {
			fail("The constants class not found");
		}
	}

	@Test(timeout = 1000)
	public void testAlterEnergyWithoutShieldAndPositiveEnergy() {

		Object schemerMonster = null;
		try {
			schemerMonster = createSchemer();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		 Field shieldedField = null;

		try {

		    shieldedField = schemerMonster.getClass().getSuperclass().getDeclaredField("shielded");
		    shieldedField.setAccessible(true);

		}
		catch (NoSuchFieldException e) {
			fail("The monster class does not have shielded attribute");
		}


		Field energyField = null;
		 int energyValueBeforeInvoking = 0;
		 int positiveEnergy = 0;

		try {
			 energyField = schemerMonster.getClass().getSuperclass().getDeclaredField("energy");
			 energyField.setAccessible(true);
			 energyValueBeforeInvoking = energyField.getInt(schemerMonster);

			 positiveEnergy = new Random().nextInt(20) + 1;

			 Method alterEnergyMethod = schemerMonster.getClass().getSuperclass().getDeclaredMethod("alterEnergy", int.class);
			 alterEnergyMethod.invoke(schemerMonster, positiveEnergy);
			 assertFalse("The monster's shield should still be deactivated",shieldedField.getBoolean(schemerMonster));


		} catch (NoSuchFieldException e) {
			fail("The monster class does not have energy attribute");
		}
		catch(NoSuchMethodException e) {
			fail("The monster class does not contain alterEnergy method which takes energy as a parameter");
		}
		catch (InvocationTargetException e) {
			fail("The alterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		 catch (IllegalAccessException e) {
			 fail(e.getClass() + " " + e.getMessage());
		}

		try {

			Field SCHEMER_STEAL_FIELD = Class.forName(constantsPath).getDeclaredField("SCHEMER_STEAL");
			int energyValueAfterInvoking = energyValueBeforeInvoking + positiveEnergy + SCHEMER_STEAL_FIELD.getInt(null);

			assertEquals("The monster's energy should be increased when it receives a healing energy",energyValueAfterInvoking ,energyField.getInt(schemerMonster));

		} catch (NoSuchFieldException e) {
			fail("The constant class should contains a MULTITASKER_BONUS variable");
		} catch (IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (ClassNotFoundException e) {
			fail("The constants class not found");
		}
	}

	@Test(timeout = 1000)
	public void testAlterEnergyWithShieldAndZeroEnergy() {

		Object dasherMonster = null;
		try {
			dasherMonster = createDasher();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		 Field shieldedField = null;

		try {

		    shieldedField = dasherMonster.getClass().getSuperclass().getDeclaredField("shielded");
		    shieldedField.setAccessible(true);

		    Method  setShielded = dasherMonster.getClass().getSuperclass().getDeclaredMethod("setShielded", boolean.class);
		    setShielded.invoke(dasherMonster, true);

		}
		catch (NoSuchFieldException e) {
			fail("The monster class does not have shielded attribute");
		}
		catch (NoSuchMethodException e) {
			fail("The monster class does not have a setShielded method");
		}
		catch (InvocationTargetException e) {
        	fail("The setShielded method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		catch (IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		}



		Field energyField = null;
		int energyValueBeforeInvoking = 0;

		try {
			 energyField = dasherMonster.getClass().getSuperclass().getDeclaredField("energy");
			 energyField.setAccessible(true);
			 energyValueBeforeInvoking = energyField.getInt(dasherMonster);

			 Method alterEnergyMethod = dasherMonster.getClass().getSuperclass().getDeclaredMethod("alterEnergy", int.class);
			 alterEnergyMethod.invoke(dasherMonster, 0);
			 assertTrue("The monster's shield should not be consumed when the energy value is zero",shieldedField.getBoolean(dasherMonster));
			 assertEquals("The monster's energy should be altered accordingly when neither damage nor healing is received",energyValueBeforeInvoking ,energyField.getInt(dasherMonster));

		} catch (NoSuchFieldException e) {
			fail("The monster class does not have energy attribute");
		}
		catch(NoSuchMethodException e) {
			fail("The monster class does not contain alterEnergy method which takes energy as a parameter");
		}
		catch (InvocationTargetException e) {
			fail("The alterEnergy method in monster class shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		 catch (IllegalAccessException e) {
			 fail(e.getClass() + " " + e.getMessage());
		}

	}



	@Test(timeout=1000)
	public void testDoorCellModifyCanisterEnergyMethodExists(){
       try {

			Class doorCell = Class.forName(doorCellPath);
			Method modifyCanisterEnergyMethod = doorCell.getDeclaredMethod("modifyCanisterEnergy", Class.forName(monsterPath),int.class);
			assertEquals("modifyCanisterEnergy method in doorCell class should be a void method", void.class, modifyCanisterEnergyMethod.getReturnType());
			assertTrue("modifyCanisterEnergy method in DoorCell class should be a public one.",Modifier.isPublic(modifyCanisterEnergyMethod.getModifiers()));

		} catch(ClassNotFoundException e){

			fail("The doorCell class not found");

		} catch (NoSuchMethodException e) {

			fail("The doorCell class does not contain modifyCanisterEnergy method which takes monster and canister energy as parameters");

		} catch (SecurityException e) {

			 fail(e.getClass() + " " + e.getMessage());

		}
	}

	@Test(timeout = 1000)
	public void testDoorCellModifyCanisterEnergyWithMonsterOfSameRoleWithShield() {

		Object dasherMonster = null;
		try {
		    dasherMonster = createDasher();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		Object doorCell = null;
		try {
			doorCell = createDoorCell();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
			}

		Method getRoleMethod = null;
		Object doorCellRole = null;
		try {
			getRoleMethod = doorCell.getClass().getDeclaredMethod("getRole");
			doorCellRole = getRoleMethod.invoke(doorCell);

		} catch (NoSuchMethodException e) {
			fail("The DoorCell class does not contain a getRole method");
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException  e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (InvocationTargetException e) {
			fail("The getRole method in DoorCell class shouldn't throw any exception but got:  "
					+ e.getCause());
		}

		Method setRoleMethod = null;
		try {
			setRoleMethod = dasherMonster.getClass().getSuperclass().getDeclaredMethod("setRole", Class.forName(rolePath));
			setRoleMethod.invoke(dasherMonster, doorCellRole);
		} catch (NoSuchMethodException e) {
			fail("The monster class does not contain a setRole method");
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (InvocationTargetException e) {
			fail("The setRole method in Monster class shouldn't throw any exception but got:  "
					+ e.getCause());
		} catch (ClassNotFoundException e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		Method setShielded = null;
		try {
			setShielded = dasherMonster.getClass().getSuperclass().getDeclaredMethod("setShielded", boolean.class);
			setShielded.invoke(dasherMonster, true);
		} catch (NoSuchMethodException e) {
			fail("The monster class does not have a setShielded method");
		}
		catch (InvocationTargetException e) {
        	fail("The setShielded method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		int canisterEnergy = new Random().nextInt(200)+1;

		Field energy = null;
		int energyValue = 0;
		try {
			energy =  dasherMonster.getClass().getSuperclass().getDeclaredField("energy");
			energy.setAccessible(true);
			energyValue = energy.getInt(dasherMonster);
		} catch (NoSuchFieldException e) {
			fail("The monster class does not contain an energy variable");
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());

		}

		Field shielded = null;
		boolean shieldedValue = false;
		try {
			shielded =  dasherMonster.getClass().getSuperclass().getDeclaredField("shielded");
			shielded.setAccessible(true);
			shieldedValue = shielded.getBoolean(dasherMonster);
		} catch (NoSuchFieldException e1) {
			fail("The monster class does not contain shielded attribute");
		} catch (IllegalAccessException | SecurityException | IllegalArgumentException e) {
			fail(e.getClass() + " " + e.getMessage());
		}


		Method modifyCanisterEnergyMethod = null;
		try {
		    modifyCanisterEnergyMethod = doorCell.getClass().getDeclaredMethod("modifyCanisterEnergy", Class.forName(monsterPath),int.class);
		    modifyCanisterEnergyMethod.invoke(doorCell, dasherMonster,canisterEnergy);
		    assertTrue("The monster should not lose its shield when it gains energy",shieldedValue);
		    assertEquals("The monster's energy should be increased when the monster lands on a door of the same role",energyValue+canisterEnergy,energy.getInt(dasherMonster));

		} catch (NoSuchMethodException e) {
			fail("The door cell class does not have a modifyCanisterEnergy method");

		} catch (InvocationTargetException e) {
        	fail("The modifyCanisterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		catch (IllegalAccessException | SecurityException | IllegalArgumentException e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (ClassNotFoundException e) {
			fail(e.getClass() + " " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testDoorCellModifyCanisterEnergyWithMonsterOfSameRoleWithOutShield() {

		Object dasherMonster = null;
		try {
		    dasherMonster = createDasher();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		Object doorCell = null;
		try {
			doorCell = createDoorCell();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		Method getRoleMethod = null;
		Object doorCellRole = null;
		try {
			getRoleMethod = doorCell.getClass().getDeclaredMethod("getRole");
			doorCellRole = getRoleMethod.invoke(doorCell);

		} catch (NoSuchMethodException e) {
			fail("The DoorCell class does not contain a getRole method");
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException  e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (InvocationTargetException e) {
			fail("The getRole method in DoorCell class shouldn't throw any exception but got:  "
					+ e.getCause());
		}

		Method setRoleMethod = null;
		try {
			setRoleMethod = dasherMonster.getClass().getSuperclass().getDeclaredMethod("setRole", Class.forName(rolePath));
			setRoleMethod.invoke(dasherMonster, doorCellRole);
		} catch (NoSuchMethodException e) {
			fail("The monster class does not contain a setRole method");
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (InvocationTargetException e) {
			fail("The setRole method in Monster class shouldn't throw any exception but got:  "
					+ e.getCause());
		} catch (ClassNotFoundException e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		int canisterEnergy = new Random().nextInt(200)+1;

		Field energy = null;
		int energyValue = 0;
		try {
			energy =  dasherMonster.getClass().getSuperclass().getDeclaredField("energy");
			energy.setAccessible(true);
			energyValue = energy.getInt(dasherMonster);
		} catch (NoSuchFieldException e) {
			fail("The monster class does not contain an energy variable");
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());

		}

		Field shielded = null;
		Object shieldedValue = null;
		try {
			shielded =  dasherMonster.getClass().getSuperclass().getDeclaredField("shielded");
			shielded.setAccessible(true);
			shieldedValue = shielded.getBoolean(dasherMonster);
		} catch (NoSuchFieldException e1) {
			fail("The monster class does not contain shielded attribute");
		} catch (IllegalAccessException | SecurityException | IllegalArgumentException e) {
			fail(e.getClass() + " " + e.getMessage());
		}


		Method modifyCanisterEnergyMethod = null;
		try {
		    modifyCanisterEnergyMethod = doorCell.getClass().getDeclaredMethod("modifyCanisterEnergy", Class.forName(monsterPath),int.class);
		    modifyCanisterEnergyMethod.invoke(doorCell, dasherMonster,canisterEnergy);
		    assertFalse("The monster's shield should still be deactivated",(boolean)shieldedValue);
		    assertEquals("The monster's energy should be increased when the monster lands on a door of the same role",energyValue+canisterEnergy,energy.getInt(dasherMonster));

		} catch (NoSuchMethodException e) {
			fail("The door cell class does not have a modifyCanisterEnergy method");

		} catch (InvocationTargetException e) {
        	fail("The modifyCanisterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		catch (IllegalAccessException | SecurityException | IllegalArgumentException e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (ClassNotFoundException e) {
			fail(e.getClass() + " " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testDoorCellModifyCanisterEnergyWithMonsterOfDifferentRoleWithShield() {

		Object dasherMonster = null;
		try {
		    dasherMonster = createDasher();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		Object doorCell = null;
		try {
			doorCell = createDoorCell();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		Method getRoleMethod = null;
		Object doorCellRole = null;
		try {
			getRoleMethod = doorCell.getClass().getDeclaredMethod("getRole");
			doorCellRole = getRoleMethod.invoke(doorCell);

		} catch (NoSuchMethodException e) {
			fail("The DoorCell class does not contain a getRole method");
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException  e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (InvocationTargetException e) {
			fail("The getRole method in DoorCell class shouldn't throw any exception but got:  "
					+ e.getCause());
		}

		Method setRoleMethod = null;
		try {
			setRoleMethod = dasherMonster.getClass().getSuperclass().getDeclaredMethod("setRole", Class.forName(rolePath));
			if(doorCellRole.equals(Enum.valueOf((Class<Enum>) Class.forName(rolePath), "LAUGHER")))
				setRoleMethod.invoke(dasherMonster, Enum.valueOf((Class<Enum>) Class.forName(rolePath), "SCARER"));
			else
				setRoleMethod.invoke(dasherMonster, Enum.valueOf((Class<Enum>) Class.forName(rolePath), "LAUGHER"));

		} catch (NoSuchMethodException e) {
			fail("The monster class does not contain a setRole method");
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (InvocationTargetException e) {
			fail("The setRole method in Monster class shouldn't throw any exception but got:  "
					+ e.getCause());
		} catch (ClassNotFoundException e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		Method setShielded = null;
		try {
			setShielded = dasherMonster.getClass().getSuperclass().getDeclaredMethod("setShielded", boolean.class);
			setShielded.invoke(dasherMonster, true);
		} catch (NoSuchMethodException e) {
			fail("The monster class does not have a setShielded method");
		}
		catch (InvocationTargetException e) {
	    	fail("The setShielded method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		int canisterEnergy = new Random().nextInt(200)+1;

		Field energy = null;
		int energyValue = 0;
		try {
			energy =  dasherMonster.getClass().getSuperclass().getDeclaredField("energy");
			energy.setAccessible(true);
			energyValue = energy.getInt(dasherMonster);
		} catch (NoSuchFieldException e) {
			fail("The monster class does not contain an energy variable");
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());

		}

		Method modifyCanisterEnergyMethod = null;
		try {
		    modifyCanisterEnergyMethod = doorCell.getClass().getDeclaredMethod("modifyCanisterEnergy", Class.forName(monsterPath),int.class);
		    modifyCanisterEnergyMethod.invoke(doorCell, dasherMonster,canisterEnergy);

		} catch (NoSuchMethodException e) {
			fail("The door cell class does not have a modifyCanisterEnergy method");

		} catch (InvocationTargetException e) {
	    	fail("The modifyCanisterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		} catch (IllegalAccessException | SecurityException | IllegalArgumentException e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (ClassNotFoundException e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		Field shielded = null;
		Object shieldedValue = null;
		int energyValueAfterInvoke = 0;
		try {
			shielded =  dasherMonster.getClass().getSuperclass().getDeclaredField("shielded");
			shielded.setAccessible(true);
			shieldedValue = shielded.getBoolean(dasherMonster);
			energyValueAfterInvoke = energy.getInt(dasherMonster);
		} catch (NoSuchFieldException e1) {
			fail("The monster class does not contain shielded attribute");
		} catch (IllegalAccessException | SecurityException | IllegalArgumentException e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		 assertFalse("The monster should lose its shield when it receives a damage",(boolean)shieldedValue);
		 assertEquals("The monster's energy should not be altered when the monster is shielded",energyValue,energyValueAfterInvoke);

	}

	@Test(timeout = 1000)
	public void testDoorCellModifyCanisterEnergyWithMonsterOfDifferentRoleWithOutShield() {

		Object dasherMonster = null;
		try {
		    dasherMonster = createDasher();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		Object doorCell = null;
		try {
			doorCell = createDoorCell();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		Method getRoleMethod = null;
		Object doorCellRole = null;
		try {
			getRoleMethod = doorCell.getClass().getDeclaredMethod("getRole");
			doorCellRole = getRoleMethod.invoke(doorCell);

		} catch (NoSuchMethodException e) {
			fail("The DoorCell class does not contain a getRole method");
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException  e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (InvocationTargetException e) {
			fail("The getRole method in DoorCell class shouldn't throw any exception but got:  "
					+ e.getCause());
		}

		Method setRoleMethod = null;
		try {
			setRoleMethod = dasherMonster.getClass().getSuperclass().getDeclaredMethod("setRole", Class.forName(rolePath));
			if(doorCellRole.equals(Enum.valueOf((Class<Enum>) Class.forName(rolePath), "LAUGHER")))
				setRoleMethod.invoke(dasherMonster, Enum.valueOf((Class<Enum>) Class.forName(rolePath), "SCARER"));
			else
				setRoleMethod.invoke(dasherMonster, Enum.valueOf((Class<Enum>) Class.forName(rolePath), "LAUGHER"));

		} catch (NoSuchMethodException e) {
			fail("The monster class does not contain a setRole method");
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (InvocationTargetException e) {
			fail("The setRole method in Monster class shouldn't throw any exception but got:  "
					+ e.getCause());
		} catch (ClassNotFoundException e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		int canisterEnergy = new Random().nextInt(200)+1;

		Field energy = null;
		int energyValue = 0;
		try {
			energy =  dasherMonster.getClass().getSuperclass().getDeclaredField("energy");
			energy.setAccessible(true);
			energyValue = energy.getInt(dasherMonster);
		} catch (NoSuchFieldException e) {
			fail("The monster class does not contain an energy variable");
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());

		}

		Method modifyCanisterEnergyMethod = null;
		try {
		    modifyCanisterEnergyMethod = doorCell.getClass().getDeclaredMethod("modifyCanisterEnergy", Class.forName(monsterPath),int.class);
		    modifyCanisterEnergyMethod.invoke(doorCell, dasherMonster,canisterEnergy);

		} catch (NoSuchMethodException e) {
			fail("The door cell class does not have a modifyCanisterEnergy method");

		} catch (InvocationTargetException e) {
	    	fail("The modifyCanisterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		} catch (IllegalAccessException | SecurityException | IllegalArgumentException e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (ClassNotFoundException e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		Field shielded = null;
		Object shieldedValue = null;
		int energyValueAfterInvokeExpected = energyValue+(-canisterEnergy);
		if(energyValueAfterInvokeExpected<0)
			energyValueAfterInvokeExpected = 0;
		int energyValueAfterInvokeActual = 0;
		try {
			shielded =  dasherMonster.getClass().getSuperclass().getDeclaredField("shielded");
			shielded.setAccessible(true);
			shieldedValue = shielded.getBoolean(dasherMonster);
			energyValueAfterInvokeActual = energy.getInt(dasherMonster);
		} catch (NoSuchFieldException e1) {
			fail("The monster class does not contain shielded attribute");
		} catch (IllegalAccessException | SecurityException | IllegalArgumentException e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		 assertFalse("The monster's shield remain deactivated",(boolean)shieldedValue);
		 assertEquals("The monster's energy should be decreased when the monster receives a damage and it is not shielded",energyValueAfterInvokeExpected,energyValueAfterInvokeActual);

	}


	@Test(timeout = 1000)
	public void testContaminationSockModifyCanisterEnergyWithShieldAndPositiveEnergy() {


		Object multiTaskerMonster = null;
		try {
			multiTaskerMonster = createMultiTasker();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		 Field shieldedField = null;

		try {

		    shieldedField = multiTaskerMonster.getClass().getSuperclass().getDeclaredField("shielded");
		    shieldedField.setAccessible(true);

		    Method  setShielded = multiTaskerMonster.getClass().getSuperclass().getDeclaredMethod("setShielded", boolean.class);
		    setShielded.invoke(multiTaskerMonster, true);

		}
		catch (NoSuchFieldException e) {
			fail("The monster class does not have shielded attribute");
		}
		catch (NoSuchMethodException e) {
			fail("The monster class does not have a setShielded method");
		}
		catch (InvocationTargetException e) {
        	fail("The setShielded method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		catch (IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		}



		Field energyField = null;
		 int energyValueBeforeInvoking = 0;
		 int positiveEnergy = 0;

		try {
			 energyField = multiTaskerMonster.getClass().getSuperclass().getDeclaredField("energy");
			 energyField.setAccessible(true);
			 energyValueBeforeInvoking = energyField.getInt(multiTaskerMonster);

			 positiveEnergy = new Random().nextInt(200) + 1;

			 Object contaminationSock = createContaminationSock();
			 Method modifyCanisterEnergyMethod = contaminationSock.getClass().getDeclaredMethod("modifyCanisterEnergy",Class.forName(monsterPath) ,int.class);
			 modifyCanisterEnergyMethod.invoke(contaminationSock, multiTaskerMonster, positiveEnergy);
			 assertTrue("The monster should not lose its shield if it receives a healing energy",shieldedField.getBoolean(multiTaskerMonster));


		} catch (NoSuchFieldException e) {
			fail("The monster class does not have energy attribute");
		}
		catch(NoSuchMethodException e) {
			fail("The EnergyStealCard class does not contain modifyCanisterEnergy method");
		}
		catch (InvocationTargetException e) {
			fail("The modifyCanisterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		 catch (IllegalAccessException e) {
			 fail(e.getClass() + " " + e.getMessage());
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		try {

			Field MULTITASKER_BONUS_FIELD = Class.forName(constantsPath).getDeclaredField("MULTITASKER_BONUS");
			int energyValueAfterInvoking = energyValueBeforeInvoking + positiveEnergy + MULTITASKER_BONUS_FIELD.getInt(null);

			assertEquals("The monster's energy should be increased when it receives a healing energy",energyValueAfterInvoking ,energyField.getInt(multiTaskerMonster));

		} catch (NoSuchFieldException e) {
			fail("The constant class should contains a MULTITASKER_BONUS variable");
		} catch (IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		} catch (ClassNotFoundException e) {
			fail("The constants class not found");
		}
	}

	@Test(timeout = 1000)
	public void testContaminationSockModifyCanisterEnergyWithoutShieldAndPositiveEnergy() {

		Object dasherMonster = null;
		try {
			dasherMonster = createDasher();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		 Field shieldedField = null;

		try {

		    shieldedField = dasherMonster.getClass().getSuperclass().getDeclaredField("shielded");
		    shieldedField.setAccessible(true);

		}
		catch (NoSuchFieldException e) {
			fail("The monster class does not have shielded attribute");
		}


		Field energyField = null;
		 int energyValueBeforeInvoking = 0;
		 int positiveEnergy = 0;

		try {
			 energyField = dasherMonster.getClass().getSuperclass().getDeclaredField("energy");
			 energyField.setAccessible(true);
			 energyValueBeforeInvoking = energyField.getInt(dasherMonster);

			 positiveEnergy = new Random().nextInt(200) + 1;


			 Object contaminationSock = createContaminationSock();
			 Method modifyCanisterEnergyMethod = contaminationSock.getClass().getDeclaredMethod("modifyCanisterEnergy",Class.forName(monsterPath) ,int.class);
			 modifyCanisterEnergyMethod.invoke(contaminationSock, dasherMonster, positiveEnergy);
			 int energyValueAfterInvoking = energyValueBeforeInvoking + positiveEnergy;
			 assertFalse("The monster's shield should still be deactivated when it receives healing energy",shieldedField.getBoolean(dasherMonster));
			 assertEquals("The monster's energy should be increased when it receives a healing energy",energyValueAfterInvoking ,energyField.getInt(dasherMonster));


		} catch (NoSuchFieldException e) {
			fail("The monster class does not have energy attribute");
		}
		catch(NoSuchMethodException e) {
			fail("The EnergyStealCard class does not contain modifyCanisterEnergy method");
		}
		catch (InvocationTargetException e) {
			fail("The modifyCanisterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		 catch (IllegalAccessException e) {
			 fail(e.getClass() + " " + e.getMessage());
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());

		}

	}

	@Test(timeout = 1000)
	public void testContaminationSockModifyCanisterEnergyWithShieldAndZeroEnergy() {

		Object dasherMonster = null;
		try {
			dasherMonster = createDasher();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		 Field shieldedField = null;

		try {

		    shieldedField = dasherMonster.getClass().getSuperclass().getDeclaredField("shielded");
		    shieldedField.setAccessible(true);

		    Method  setShielded = dasherMonster.getClass().getSuperclass().getDeclaredMethod("setShielded", boolean.class);
		    setShielded.invoke(dasherMonster, true);

		}
		catch (NoSuchFieldException e) {
			fail("The monster class does not have shielded attribute");
		}
		catch (NoSuchMethodException e) {
			fail("The monster class does not have a setShielded method");
		}
		catch (InvocationTargetException e) {
        	fail("The setShielded method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		catch (IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		}



		Field energyField = null;
		int energyValueBeforeInvoking = 0;

		try {
			 energyField = dasherMonster.getClass().getSuperclass().getDeclaredField("energy");
			 energyField.setAccessible(true);
			 energyValueBeforeInvoking = energyField.getInt(dasherMonster);

			 Object contaminationSock = createContaminationSock();
			 Method modifyCanisterEnergyMethod = contaminationSock.getClass().getDeclaredMethod("modifyCanisterEnergy",Class.forName(monsterPath) ,int.class);
			 modifyCanisterEnergyMethod.invoke(contaminationSock, dasherMonster, 0);
			 assertTrue("The monster's shield should not be consumed when the energy value is zero",shieldedField.getBoolean(dasherMonster));
			 assertEquals("The monster's energy should be altered accordingly when neither damage nor healing is received",energyValueBeforeInvoking ,energyField.getInt(dasherMonster));

		} catch (NoSuchFieldException e) {
			fail("The monster class does not have energy attribute");
		}
		catch(NoSuchMethodException e) {
			fail("The EnergyStealCard class does not contain modifyCanisterEnergy method");
		}
		catch (InvocationTargetException e) {
			fail("The modifyCanisterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		 catch (IllegalAccessException e) {
			 fail(e.getClass() + " " + e.getMessage());
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());

		}

	}


	@Test(timeout=1000)
	public void testEnergyStealCardModifyCanisterEnergyMethodExists(){
       try {

			Class energyStealCard = Class.forName(energyStealCardPath);
			Method modifyCanisterEnergyMethod = energyStealCard.getDeclaredMethod("modifyCanisterEnergy", Class.forName(monsterPath),int.class);
			assertEquals("modifyCanisterEnergy method in energyStealCard class should be a void method", void.class, modifyCanisterEnergyMethod.getReturnType());
			assertTrue("modifyCanisterEnergy method in EnergStealCard class should be a public one.",Modifier.isPublic(modifyCanisterEnergyMethod.getModifiers()));

		} catch(ClassNotFoundException e){

			fail("The energyStealCard class not found");

		} catch (NoSuchMethodException e) {

			fail("The energyStealCard class does not contain modifyCanisterEnergy method which takes monster and canister energy as parameters");

		} catch (SecurityException e) {

			 fail(e.getClass() + " " + e.getMessage());

		}
	}

	@Test(timeout = 1000)
	public void testEnergyStealCardModifyCanisterEnergyWithShieldAndNegativeEnergy() {

		Object dasherMonster = null;
		try {
			dasherMonster = createDasher();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		 Field shieldedField = null;

		try {

		    shieldedField = dasherMonster.getClass().getSuperclass().getDeclaredField("shielded");
		    shieldedField.setAccessible(true);

		    Method  setShielded = dasherMonster.getClass().getSuperclass().getDeclaredMethod("setShielded", boolean.class);
		    setShielded.invoke(dasherMonster, true);

		}
		catch (NoSuchFieldException e) {
			fail("The monster class does not have shielded attribute");
		}
		catch (NoSuchMethodException e) {
			fail("The monster class does not have a setShielded method");
		}
		catch (InvocationTargetException e) {
        	fail("The setShielded method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		catch (IllegalAccessException e) {
			fail(e.getClass() + " " + e.getMessage());
		}



		Field energyField = null;

		try {
			 energyField = dasherMonster.getClass().getSuperclass().getDeclaredField("energy");
			 energyField.setAccessible(true);
			 int energyValueBeforeInvoking = energyField.getInt(dasherMonster);

			 int negativeEnergy = (new Random().nextInt(100) + 1) * -1;

			 Object energyStealCard = createEnergyStealCard();
			 Method modifyCanisterEnergyMethod = energyStealCard.getClass().getDeclaredMethod("modifyCanisterEnergy",Class.forName(monsterPath) ,int.class);
			 modifyCanisterEnergyMethod.invoke(energyStealCard, dasherMonster, negativeEnergy);
			 assertFalse("The monster should lose its shield if it receives a damage",shieldedField.getBoolean(dasherMonster));
			 assertEquals("The monster's energy should not be effected when it is shielded",energyValueBeforeInvoking ,energyField.getInt(dasherMonster));

		} catch (NoSuchFieldException e) {
			fail("The monster class does not have energy attribute");
		}
		catch(NoSuchMethodException e) {
			fail("The EnergyStealCard class does not contain modifyCanisterEnergy method");
		}
		catch (InvocationTargetException e) {
			fail("The modifyCanisterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		 catch (IllegalAccessException e) {
			 fail(e.getClass() + " " + e.getMessage());
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testEnergyStealCardModifyCanisterEnergyWithoutShieldAndNegativeEnergy() {

		Object dynamoMonster = null;
		try {
			dynamoMonster = createDynamo();
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}

		 Field shieldedField = null;

		try {

		    shieldedField = dynamoMonster.getClass().getSuperclass().getDeclaredField("shielded");
		    shieldedField.setAccessible(true);

		}
		catch (NoSuchFieldException e) {
			fail("The monster class does not have shielded attribute");
		}


		Field energyField = null;

		try {
			 energyField = dynamoMonster.getClass().getSuperclass().getDeclaredField("energy");
			 energyField.setAccessible(true);
			 int energyValueBeforeInvoking = energyField.getInt(dynamoMonster);

			 int negativeEnergy = (new Random().nextInt(100) + 1) * -1;

			 int energyValueAfterInvoking = energyValueBeforeInvoking + negativeEnergy*2;
			 if(energyValueAfterInvoking < 0)
				 energyValueAfterInvoking = 0;


			 Object energyStealCard = createEnergyStealCard();
			 Method modifyCanisterEnergyMethod = energyStealCard.getClass().getDeclaredMethod("modifyCanisterEnergy",Class.forName(monsterPath) ,int.class);
			 modifyCanisterEnergyMethod.invoke(energyStealCard, dynamoMonster, negativeEnergy);
			 assertFalse("The monster's shield should still be deactivated ",shieldedField.getBoolean(dynamoMonster));
			 assertEquals("The monster should receive a damage that alters its energy when it is not shielded",energyValueAfterInvoking ,energyField.getInt(dynamoMonster));

		} catch (NoSuchFieldException e) {
			fail("The monster class does not have energy attribute");
		}
		catch(NoSuchMethodException e) {
			fail("The EnergyStealCard class does not contain modifyCanisterEnergy method");
		}
		catch (InvocationTargetException e) {
			fail("The modifyCanisterEnergy method shouldn't throw any exception but got:  "
					+ e.getCause());
		}
		 catch (IllegalAccessException e) {
			 fail(e.getClass() + " " + e.getMessage());
		} catch (Exception e) {
			fail(e.getClass() + " " + e.getMessage());
		}
	}


	@Test(timeout = 1000)
	public void testDoorCellOnLandMethodExist() {
		try {
			Class doorCell = Class.forName(doorCellPath);
			Method onLand = doorCell.getDeclaredMethod("onLand", Class.forName(monsterPath), Class.forName(monsterPath));
			assertEquals("The onLand method should be a void method",void.class,onLand.getReturnType());
			assertTrue("onLand method in DorrCell class should be a public one.",Modifier.isPublic(onLand.getModifiers()));

		} catch (ClassNotFoundException e) {
			fail("DoorCell class not found");
		} catch (NoSuchMethodException e) {
			fail("Method onLand not found in DoorCell class");
		} catch (SecurityException e) {
			fail(e.getClass() + " " + e.getMessage());
		}

	}

	@Test(timeout = 1000)
	public void testDoorCellOnLandMethodWhenDoorIsActivated() {
		Object game = null;
		try {
			game = createGameForTesting();

			Method getBoard = game.getClass().getDeclaredMethod("getBoard");
			Object board = getBoard.invoke(game);

			Method getBoardCells = board.getClass().getDeclaredMethod("getBoardCells");
			Object[][] boardCells = (Object[][]) getBoardCells.invoke(board);

			int rowNumber = (new Random().nextInt(6)*2)%10;
			int columnNumber = (new Random().nextInt(6)*2+1)%10;
			Object doorCell = boardCells[rowNumber][columnNumber];

			Method getCurrent = game.getClass().getDeclaredMethod("getCurrent");
			Object currentPlayer = getCurrent.invoke(game);

			Method getCurrentOpponent = game.getClass().getDeclaredMethod("getCurrentOpponent");
			getCurrentOpponent.setAccessible(true);
			Object opponentPlayer = getCurrentOpponent.invoke(game);

			Method getStationedMonsters = board.getClass().getDeclaredMethod("getStationedMonsters");
			ArrayList<Object> stationedMonsters = (ArrayList<Object>) getStationedMonsters.invoke(board);

			Method getEnergy = currentPlayer.getClass().getSuperclass().getDeclaredMethod("getEnergy");
			int landingMonsterEnergy = (int) getEnergy.invoke(currentPlayer);

			Method getRole = Class.forName(monsterPath).getDeclaredMethod("getRole");
			Object landingMonsterRole = getRole.invoke(currentPlayer);

			LinkedHashMap<Object, Integer> monsterEnergyMap = new LinkedHashMap<>();

			for(int i = stationedMonsters.size()-1; i >= 0; i--) {
			    Object stationedMonsterRole = getRole.invoke(stationedMonsters.get(i));
			    if(stationedMonsterRole.equals(landingMonsterRole)) {
			        monsterEnergyMap.put(
			            stationedMonsters.get(i),
			            (Integer) getEnergy.invoke(stationedMonsters.get(i))
			        );
			    }
			}


			Method setActivated = Class.forName(doorCellPath).getDeclaredMethod("setActivated", boolean.class);
			setActivated.invoke(doorCell, true);

			Method onLand = Class.forName(doorCellPath).getDeclaredMethod("onLand",Class.forName(monsterPath),Class.forName(monsterPath));
			onLand.invoke(doorCell, currentPlayer,opponentPlayer);

			Field cellMonster = Class.forName(cellPath).getDeclaredField("monster");
			cellMonster.setAccessible(true);
			Object cellMonsterValue = cellMonster.get(doorCell);

			assertEquals("The monster of the doorCell should be the current landing monster",currentPlayer,cellMonsterValue);
			assertEquals("Once a door cell has been used, landing on it again should not affect the landing monster",landingMonsterEnergy,getEnergy.invoke(currentPlayer));

			for(Map.Entry<Object, Integer> entry : monsterEnergyMap.entrySet()) {
			    Object monster = entry.getKey();
			    Integer energy = entry.getValue();
			    assertEquals("Once a door cell has been used, landing on it again should not affect the landing monster nor its team",energy,getEnergy.invoke(monster));
			}

			Field activated = Class.forName(doorCellPath).getDeclaredField("activated");
			activated.setAccessible(true);
			assertTrue("Once a door cell has been activated, it should never return to its inactive state",activated.getBoolean(doorCell));

		} catch (Exception e) {
			fail(e.getClass()+ " " + e.getMessage());
		}


	}

	@Test(timeout = 1000)
	public void testDoorCellOnLandMethodWhenDoorIsNotActivatedLandingMonsterOfSameRoleWithShield() {
		Object game = null;
		try {
			game = createGameForTesting();

			Method getBoard = game.getClass().getDeclaredMethod("getBoard");
			Object board = getBoard.invoke(game);

			Method getBoardCells = board.getClass().getDeclaredMethod("getBoardCells");
			Object[][] boardCells = (Object[][]) getBoardCells.invoke(board);

			int rowNumber = (new Random().nextInt(6)*2)%10;
			int columnNumber = (new Random().nextInt(6)*2+1)%10;
			Object doorCell = boardCells[rowNumber][columnNumber];

			Method getCurrent = game.getClass().getDeclaredMethod("getCurrent");
			Object currentPlayer = getCurrent.invoke(game);

			Method getCurrentOpponent = game.getClass().getDeclaredMethod("getCurrentOpponent");
			getCurrentOpponent.setAccessible(true);
			Object opponentPlayer = getCurrentOpponent.invoke(game);

			Method getStationedMonsters = board.getClass().getDeclaredMethod("getStationedMonsters");
			ArrayList<Object> stationedMonsters = (ArrayList<Object>) getStationedMonsters.invoke(board);

			Method getRoleCell = Class.forName(doorCellPath).getDeclaredMethod("getRole");
			Object doorCellRole = getRoleCell.invoke(doorCell);

			Method setRole= Class.forName(monsterPath).getDeclaredMethod("setRole", Class.forName(rolePath));
			setRole.invoke(currentPlayer, doorCellRole);

			Method getEnergy = currentPlayer.getClass().getSuperclass().getDeclaredMethod("getEnergy");

			Method getRole = Class.forName(monsterPath).getDeclaredMethod("getRole");

			LinkedHashMap<Object, Integer> monsterEnergyMap = new LinkedHashMap<>();

			for(int i = stationedMonsters.size()-1; i >= 0; i--) {
			    Object stationedMonsterRole = getRole.invoke(stationedMonsters.get(i));
			    if(stationedMonsterRole.equals(doorCellRole)) {
			        monsterEnergyMap.put(
			            stationedMonsters.get(i),
			            (Integer) getEnergy.invoke(stationedMonsters.get(i))
			        );
			    }
			}

			Method setShield = Class.forName(monsterPath).getDeclaredMethod("setShielded", boolean.class);
			setShield.invoke(currentPlayer, true);

			Method setActivated = Class.forName(doorCellPath).getDeclaredMethod("setActivated", boolean.class);
			setActivated.invoke(doorCell, false);

			Object landingMonsterEnergy = getEnergy.invoke(currentPlayer);

			Method getEnergyDoor = Class.forName(doorCellPath).getDeclaredMethod("getEnergy");
			Object doorCellEnergy = getEnergyDoor.invoke(doorCell);

			int expectedEnergy = 0;
			if(currentPlayer.getClass().equals(Class.forName(dasherPath))){
				expectedEnergy = (int)landingMonsterEnergy + (int)doorCellEnergy;
			}
			else if(currentPlayer.getClass().equals(Class.forName(schemerPath))){
				expectedEnergy = (int)landingMonsterEnergy + (int)doorCellEnergy + 10;
			}
			else if(currentPlayer.getClass().equals(Class.forName(dynamoPath))) {
				expectedEnergy = (int)landingMonsterEnergy + (int)doorCellEnergy*2;
			}
			else if(currentPlayer.getClass().equals(Class.forName(multiTaskerPath))) {
				expectedEnergy = (int)landingMonsterEnergy + (int)doorCellEnergy + 200;
			}


			for(Map.Entry<Object, Integer> entry : monsterEnergyMap.entrySet()) {
			    Object monster = entry.getKey();
			    Integer monsterEnergy = entry.getValue();
			    Integer updatedEnergy = 0;
			    if(monster.getClass().equals(Class.forName(dasherPath))){
			    	updatedEnergy = monsterEnergy + (int)doorCellEnergy;
				}
				else if(monster.getClass().equals(Class.forName(schemerPath))){
					updatedEnergy = monsterEnergy + (int)doorCellEnergy + 10;
				}
				else if(monster.getClass().equals(Class.forName(dynamoPath))) {
					updatedEnergy = monsterEnergy + (int)doorCellEnergy*2;
				}
				else if(monster.getClass().equals(Class.forName(multiTaskerPath))) {
					updatedEnergy = monsterEnergy + (int)doorCellEnergy + 200;
				}
			    monsterEnergyMap.put(monster, updatedEnergy);

			}


			Method onLand = Class.forName(doorCellPath).getDeclaredMethod("onLand",Class.forName(monsterPath),Class.forName(monsterPath));
			onLand.invoke(doorCell, currentPlayer,opponentPlayer);

			Field cellMonster = Class.forName(cellPath).getDeclaredField("monster");
			cellMonster.setAccessible(true);
			Object cellMonsterValue = cellMonster.get(doorCell);

			assertEquals("The monster of the doorCell should be the current landing monster",currentPlayer,cellMonsterValue);
			assertEquals("If a monster landed on a door matching the monster's role then the monster should gain the door's energy",expectedEnergy,getEnergy.invoke(currentPlayer));

			for(Map.Entry<Object, Integer> entry : monsterEnergyMap.entrySet()) {
			    Object monster = entry.getKey();
			    Integer expectedEnergyTeam = entry.getValue();
			    Integer actualEnergy = (Integer) getEnergy.invoke(monster);
			    assertEquals("If a monster landed on a door matching the monster's role then the monster should gain the door's energy as well as the monster's team",expectedEnergyTeam,actualEnergy);
			}
			Field activated = Class.forName(doorCellPath).getDeclaredField("activated");
			activated.setAccessible(true);
			assertTrue("The door cell should be activated after it has been used",activated.getBoolean(doorCell));

		} catch (Exception e) {
			fail(e.getClass()+ " " + e.getMessage());
		}


	}

	@Test(timeout = 1000)
	public void testDoorCellOnLandMethodWhenDoorIsNotActivatedLandingMonsterOfSameRoleWithoutShield() {
		Object game = null;
		try {
			game = createGameForTesting();

			Method getBoard = game.getClass().getDeclaredMethod("getBoard");
			Object board = getBoard.invoke(game);

			Method getBoardCells = board.getClass().getDeclaredMethod("getBoardCells");
			Object[][] boardCells = (Object[][]) getBoardCells.invoke(board);

			int rowNumber = (new Random().nextInt(6)*2)%10;
			int columnNumber = (new Random().nextInt(6)*2+1)%10;
			Object doorCell = boardCells[rowNumber][columnNumber];

			Method getCurrent = game.getClass().getDeclaredMethod("getCurrent");
			Object currentPlayer = getCurrent.invoke(game);

			Method getCurrentOpponent = game.getClass().getDeclaredMethod("getCurrentOpponent");
			getCurrentOpponent.setAccessible(true);
			Object opponentPlayer = getCurrentOpponent.invoke(game);

			Method getStationedMonsters = board.getClass().getDeclaredMethod("getStationedMonsters");
			ArrayList<Object> stationedMonsters = (ArrayList<Object>) getStationedMonsters.invoke(board);

			Method getRoleCell = Class.forName(doorCellPath).getDeclaredMethod("getRole");
			Object doorCellRole = getRoleCell.invoke(doorCell);

			Method setRole= Class.forName(monsterPath).getDeclaredMethod("setRole", Class.forName(rolePath));
			setRole.invoke(currentPlayer, doorCellRole);

			Method getEnergy = currentPlayer.getClass().getSuperclass().getDeclaredMethod("getEnergy");

			Method getRole = Class.forName(monsterPath).getDeclaredMethod("getRole");

			LinkedHashMap<Object, Integer> monsterEnergyMap = new LinkedHashMap<>();

			for(int i = stationedMonsters.size()-1; i >= 0; i--) {
			    Object stationedMonsterRole = getRole.invoke(stationedMonsters.get(i));
			    if(stationedMonsterRole.equals(doorCellRole)) {
			        monsterEnergyMap.put(
			            stationedMonsters.get(i),
			            (Integer) getEnergy.invoke(stationedMonsters.get(i))
			        );
			    }
			}

			Method setShield = Class.forName(monsterPath).getDeclaredMethod("setShielded", boolean.class);
			setShield.invoke(currentPlayer, false);

			Method setActivated = Class.forName(doorCellPath).getDeclaredMethod("setActivated", boolean.class);
			setActivated.invoke(doorCell, false);

			Object landingMonsterEnergy = getEnergy.invoke(currentPlayer);

			Method getEnergyDoor = Class.forName(doorCellPath).getDeclaredMethod("getEnergy");
			Object doorCellEnergy = getEnergyDoor.invoke(doorCell);

			int expectedEnergy = 0;
			if(currentPlayer.getClass().equals(Class.forName(dasherPath))){
				expectedEnergy = (int)landingMonsterEnergy + (int)doorCellEnergy;
			}
			else if(currentPlayer.getClass().equals(Class.forName(schemerPath))){
				expectedEnergy = (int)landingMonsterEnergy + (int)doorCellEnergy + 10;
			}
			else if(currentPlayer.getClass().equals(Class.forName(dynamoPath))) {
				expectedEnergy = (int)landingMonsterEnergy + (int)doorCellEnergy*2;
			}
			else if(currentPlayer.getClass().equals(Class.forName(multiTaskerPath))) {
				expectedEnergy = (int)landingMonsterEnergy + (int)doorCellEnergy + 200;
			}


			for(Map.Entry<Object, Integer> entry : monsterEnergyMap.entrySet()) {
			    Object monster = entry.getKey();
			    Integer monsterEnergy = entry.getValue();
			    Integer updatedEnergy = 0;
			    if(monster.getClass().equals(Class.forName(dasherPath))){
			    	updatedEnergy = monsterEnergy + (int)doorCellEnergy;
				}
				else if(monster.getClass().equals(Class.forName(schemerPath))){
					updatedEnergy = monsterEnergy + (int)doorCellEnergy + 10;
				}
				else if(monster.getClass().equals(Class.forName(dynamoPath))) {
					updatedEnergy = monsterEnergy + (int)doorCellEnergy*2;
				}
				else if(monster.getClass().equals(Class.forName(multiTaskerPath))) {
					updatedEnergy = monsterEnergy + (int)doorCellEnergy + 200;
				}
			    monsterEnergyMap.put(monster, updatedEnergy);

			}


			Method onLand = Class.forName(doorCellPath).getDeclaredMethod("onLand",Class.forName(monsterPath),Class.forName(monsterPath));
			onLand.invoke(doorCell, currentPlayer,opponentPlayer);

			Field cellMonster = Class.forName(cellPath).getDeclaredField("monster");
			cellMonster.setAccessible(true);
			Object cellMonsterValue = cellMonster.get(doorCell);

			assertEquals("The monster of the doorCell should be the current landing monster",currentPlayer,cellMonsterValue);
			assertEquals("If a monster landed on a door matching the monster's role then the monster should gain the door's energy",expectedEnergy,getEnergy.invoke(currentPlayer));

			for(Map.Entry<Object, Integer> entry : monsterEnergyMap.entrySet()) {
			    Object monster = entry.getKey();
			    Integer expectedEnergyTeam = entry.getValue();
			    Integer actualEnergy = (Integer) getEnergy.invoke(monster);
			    assertEquals("If a monster landed on a door matching the monster's role then the monster should gain the door's energy as well as the monster's team",expectedEnergyTeam,actualEnergy);
			}
			Field activated = Class.forName(doorCellPath).getDeclaredField("activated");
			activated.setAccessible(true);
			assertTrue("The door cell should be activated after it has been used",activated.getBoolean(doorCell));

		} catch (Exception e) {
			fail(e.getClass()+ " " + e.getMessage());
		}


	}

	@Test(timeout = 1000)
	public void testDoorCellOnLandMethodWhenDoorIsNotActivatedLandingMonsterOfDifferentRoleWithShield() {
		Object game = null;
		try {
			game = createGameForTesting();

			Method getBoard = game.getClass().getDeclaredMethod("getBoard");
			Object board = getBoard.invoke(game);

			Method getBoardCells = board.getClass().getDeclaredMethod("getBoardCells");
			Object[][] boardCells = (Object[][]) getBoardCells.invoke(board);

			int rowNumber = (new Random().nextInt(6)*2)%10;
			int columnNumber = (new Random().nextInt(6)*2+1)%10;
			Object doorCell = boardCells[rowNumber][columnNumber];

			Method getCurrent = game.getClass().getDeclaredMethod("getCurrent");
			Object currentPlayer = getCurrent.invoke(game);

			Method getCurrentOpponent = game.getClass().getDeclaredMethod("getCurrentOpponent");
			getCurrentOpponent.setAccessible(true);
			Object opponentPlayer = getCurrentOpponent.invoke(game);

			Method getStationedMonsters = board.getClass().getDeclaredMethod("getStationedMonsters");
			ArrayList<Object> stationedMonsters = (ArrayList<Object>) getStationedMonsters.invoke(board);

			Method getRoleCell = Class.forName(doorCellPath).getDeclaredMethod("getRole");
			Object doorCellRole = getRoleCell.invoke(doorCell);

			Method setRole= Class.forName(monsterPath).getDeclaredMethod("setRole", Class.forName(rolePath));
			if(doorCellRole.equals(Enum.valueOf((Class<Enum>) Class.forName(rolePath), "SCARER"))){
				setRole.invoke(currentPlayer, (Enum.valueOf((Class<Enum>) Class.forName(rolePath), "LAUGHER")));
			}
			else
				setRole.invoke(currentPlayer, (Enum.valueOf((Class<Enum>) Class.forName(rolePath), "SCARER")));


			Method getEnergy = currentPlayer.getClass().getSuperclass().getDeclaredMethod("getEnergy");

			Method getRole = Class.forName(monsterPath).getDeclaredMethod("getRole");

			Object landingMonsterRole = getRole.invoke(currentPlayer);
			LinkedHashMap<Object, Integer> monsterEnergyMap = new LinkedHashMap<>();

			for(int i = stationedMonsters.size()-1; i >= 0; i--) {
			    Object stationedMonsterRole = getRole.invoke(stationedMonsters.get(i));
			    if(stationedMonsterRole.equals(landingMonsterRole)) {
			        monsterEnergyMap.put(
			            stationedMonsters.get(i),
			            (Integer) getEnergy.invoke(stationedMonsters.get(i))
			        );
			    }
			}

			Method setShield = Class.forName(monsterPath).getDeclaredMethod("setShielded", boolean.class);
			setShield.invoke(currentPlayer, true);

			Method setActivated = Class.forName(doorCellPath).getDeclaredMethod("setActivated", boolean.class);
			setActivated.invoke(doorCell, false);

			Object landingMonsterEnergyOld = getEnergy.invoke(currentPlayer);

			Method getEnergyDoor = Class.forName(doorCellPath).getDeclaredMethod("getEnergy");
			Object doorCellEnergy = getEnergyDoor.invoke(doorCell);

			Method onLand = Class.forName(doorCellPath).getDeclaredMethod("onLand",Class.forName(monsterPath),Class.forName(monsterPath));
			onLand.invoke(doorCell, currentPlayer,opponentPlayer);

			Field cellMonster = Class.forName(cellPath).getDeclaredField("monster");
			cellMonster.setAccessible(true);
			Object cellMonsterValue = cellMonster.get(doorCell);

			assertEquals("The monster of the doorCell should be the current landing monster",currentPlayer,cellMonsterValue);

			Field shielded = Class.forName(monsterPath).getDeclaredField("shielded");
			shielded.setAccessible(true);
			assertFalse("If a shielded monster lands on a door that does not match its role, the shield should be consumed",shielded.getBoolean(currentPlayer));
			assertEquals("If a shielded monster lands on a door that does not match its role, its energy should remain unchanged",landingMonsterEnergyOld,getEnergy.invoke(currentPlayer));

			for(Map.Entry<Object, Integer> entry : monsterEnergyMap.entrySet()) {
			    Object monster = entry.getKey();
			    Integer expectedEnergyTeam = entry.getValue();
			    Integer actualEnergy = (Integer) getEnergy.invoke(monster);
			    assertEquals("If a shielded monster lands on a door that does not match its role, its energy should remain unchanged as well as its team",expectedEnergyTeam,actualEnergy);
			}

			Field activated = Class.forName(doorCellPath).getDeclaredField("activated");
			activated.setAccessible(true);
			assertFalse("The door cell should not be activated if the landing monster is shielded",activated.getBoolean(doorCell));

		} catch (Exception e) {
			fail(e.getClass()+ " " + e.getMessage());
		}


	}

	@Test(timeout = 1000)
	public void testDoorCellOnLandMethodWhenDoorIsNotActivatedLandingMonsterOfDifferentRoleWithoutShield() {
		Object game = null;
		try {
			game = createGameForTesting();

			Method getBoard = game.getClass().getDeclaredMethod("getBoard");
			Object board = getBoard.invoke(game);

			Method getBoardCells = board.getClass().getDeclaredMethod("getBoardCells");
			Object[][] boardCells = (Object[][]) getBoardCells.invoke(board);

			int rowNumber = (new Random().nextInt(6)*2)%10;
			int columnNumber = (new Random().nextInt(6)*2+1)%10;
			Object doorCell = boardCells[rowNumber][columnNumber];

			Method getCurrent = game.getClass().getDeclaredMethod("getCurrent");
			Object currentPlayer = getCurrent.invoke(game);

			Method getCurrentOpponent = game.getClass().getDeclaredMethod("getCurrentOpponent");
			getCurrentOpponent.setAccessible(true);
			Object opponentPlayer = getCurrentOpponent.invoke(game);

			Method getStationedMonsters = board.getClass().getDeclaredMethod("getStationedMonsters");
			ArrayList<Object> stationedMonsters = (ArrayList<Object>) getStationedMonsters.invoke(board);

			Method getRoleCell = Class.forName(doorCellPath).getDeclaredMethod("getRole");
			Object doorCellRole = getRoleCell.invoke(doorCell);

			Method setRole= Class.forName(monsterPath).getDeclaredMethod("setRole", Class.forName(rolePath));
			if(doorCellRole.equals(Enum.valueOf((Class<Enum>) Class.forName(rolePath), "SCARER"))){
				setRole.invoke(currentPlayer, (Enum.valueOf((Class<Enum>) Class.forName(rolePath), "LAUGHER")));
			}
			else
				setRole.invoke(currentPlayer, (Enum.valueOf((Class<Enum>) Class.forName(rolePath), "SCARER")));


			Method getEnergy = currentPlayer.getClass().getSuperclass().getDeclaredMethod("getEnergy");

			Method getRole = Class.forName(monsterPath).getDeclaredMethod("getRole");

			Object landingMonsterRole = getRole.invoke(currentPlayer);
			LinkedHashMap<Object, Integer> monsterEnergyMap = new LinkedHashMap<>();

			for(int i = stationedMonsters.size()-1; i >= 0; i--) {
			    Object stationedMonsterRole = getRole.invoke(stationedMonsters.get(i));
			    if(stationedMonsterRole.equals(landingMonsterRole)) {
			        monsterEnergyMap.put(
			            stationedMonsters.get(i),
			            (Integer) getEnergy.invoke(stationedMonsters.get(i))
			        );
			    }
			}

			Method setShield = Class.forName(monsterPath).getDeclaredMethod("setShielded", boolean.class);
			setShield.invoke(currentPlayer, false);

			Method setActivated = Class.forName(doorCellPath).getDeclaredMethod("setActivated", boolean.class);
			setActivated.invoke(doorCell, false);

			Object landingMonsterEnergyOld = getEnergy.invoke(currentPlayer);

			Method getEnergyDoor = Class.forName(doorCellPath).getDeclaredMethod("getEnergy");
			Object doorCellEnergy = getEnergyDoor.invoke(doorCell);

			int expectedEnergyOfLandingMonster = 0;
			if(currentPlayer.getClass().equals(Class.forName(dasherPath))){
				expectedEnergyOfLandingMonster = (int)landingMonsterEnergyOld - (int)doorCellEnergy;
			}
			else if(currentPlayer.getClass().equals(Class.forName(schemerPath))){
				expectedEnergyOfLandingMonster = (int)landingMonsterEnergyOld - (int)doorCellEnergy + 10;
			}
			else if(currentPlayer.getClass().equals(Class.forName(dynamoPath))) {
				expectedEnergyOfLandingMonster = (int)landingMonsterEnergyOld - (int)doorCellEnergy*2;
			}
			else if(currentPlayer.getClass().equals(Class.forName(multiTaskerPath))) {
				expectedEnergyOfLandingMonster = (int)landingMonsterEnergyOld - (int)doorCellEnergy + 200;
			}
			if(expectedEnergyOfLandingMonster<0)
				expectedEnergyOfLandingMonster=0;


			for(Map.Entry<Object, Integer> entry : monsterEnergyMap.entrySet()) {
			    Object monster = entry.getKey();
			    Integer monsterEnergy = entry.getValue();
			    Integer updatedEnergy = 0;
			    if(monster.getClass().equals(Class.forName(dasherPath))){
			    	updatedEnergy = monsterEnergy + -((int)doorCellEnergy);
				}
				else if(monster.getClass().equals(Class.forName(schemerPath))){
					updatedEnergy = monsterEnergy + -((int)doorCellEnergy) + 10;
				}
				else if(monster.getClass().equals(Class.forName(dynamoPath))) {
					updatedEnergy = monsterEnergy + -((int)doorCellEnergy)*2;
				}
				else if(monster.getClass().equals(Class.forName(multiTaskerPath))) {
					updatedEnergy = monsterEnergy + -((int)doorCellEnergy) + 200;
				}
			    if(updatedEnergy<0)
			    	updatedEnergy=0;
			    monsterEnergyMap.put(monster, updatedEnergy);

			}

			Method onLand = Class.forName(doorCellPath).getDeclaredMethod("onLand",Class.forName(monsterPath),Class.forName(monsterPath));
			onLand.invoke(doorCell, currentPlayer,opponentPlayer);

			Field cellMonster = Class.forName(cellPath).getDeclaredField("monster");
			cellMonster.setAccessible(true);
			Object cellMonsterValue = cellMonster.get(doorCell);

			assertEquals("The monster of the doorCell should be the current landing monster",currentPlayer,cellMonsterValue);

			Field shielded = Class.forName(monsterPath).getDeclaredField("shielded");
			shielded.setAccessible(true);
			assertFalse("If an unshielded monster lands on a door that does not match its role, the shield should not be altered",shielded.getBoolean(currentPlayer));
			assertEquals("If an unshielded monster lands on a door that does not match its role, its energy should be reduced by the door cell's energy",expectedEnergyOfLandingMonster,getEnergy.invoke(currentPlayer));

			for(Map.Entry<Object, Integer> entry : monsterEnergyMap.entrySet()) {
			    Object monster = entry.getKey();
			    Integer expectedEnergyTeam = entry.getValue();
			    Integer actualEnergy = (Integer) getEnergy.invoke(monster);
			    assertEquals("If an unshielded monster lands on a door that does not match its role, its energy should be reduced by the door cell's energy as well as its team",expectedEnergyTeam,actualEnergy);
			}

			Field activated = Class.forName(doorCellPath).getDeclaredField("activated");
			activated.setAccessible(true);
			assertTrue("The door cell should be activated if the landing monster is not shielded",activated.getBoolean(doorCell));

		} catch (Exception e) {
			fail(e.getClass()+ " " + e.getMessage());
		}


	}


	@Test(timeout=1000)
	public void testContaminationSockTransportMethodExists(){
       try {

			Class contaminationSock = Class.forName(contaminationSockPath);
			Method transportMethod = contaminationSock.getDeclaredMethod("transport", Class.forName(monsterPath));
			assertEquals("transport method in contamination sock class should be a void method", void.class, transportMethod.getReturnType());
			assertTrue("transport method in ContaminationSock class should be a public one.",Modifier.isPublic(transportMethod.getModifiers()));


		} catch(ClassNotFoundException e){

			fail("The contamination sock class not found");

		} catch (NoSuchMethodException e) {

			fail("The contamination sock class does not contain transport method which takes monster as a parameter");

		} catch (SecurityException e) {

			 fail(e.getClass() + " " + e.getMessage());

		}
	}

	@Test(timeout=1000)
	public void testContaminationSockTransportMethodWithShield(){

		try {
			Object contaminationSock = createContaminationSock();

			Object dasherMonster = createDasher();

			int position = new Random().nextInt(5);
			int[] sockCellIndicies = {32,42,74,84,98};
			Method setPosition = Class.forName(monsterPath).getDeclaredMethod("setPosition", int.class);
			setPosition.invoke(dasherMonster, sockCellIndicies[position]);

			Method setShielded = Class.forName(monsterPath).getDeclaredMethod("setShielded", boolean.class);
			setShielded.invoke(dasherMonster, true);

			Method getEnergy = Class.forName(monsterPath).getDeclaredMethod("getEnergy");
			Object energyBeforeInvoke = getEnergy.invoke(dasherMonster);

			Method getEffect = Class.forName(transportCellPath).getDeclaredMethod("getEffect");
			Object effect = getEffect.invoke(contaminationSock);

			int expectedPosition = sockCellIndicies[position] + (int) effect;

			Method transport = Class.forName(contaminationSockPath).getDeclaredMethod("transport", Class.forName(monsterPath));
			transport.invoke(contaminationSock, dasherMonster);

			Method getPosition = Class.forName(monsterPath).getDeclaredMethod("getPosition");
			Object actualPosition = getPosition.invoke(dasherMonster);

			assertEquals("Landing on a contamination sock cell should cause the monster to slip back a number of steps equal to the cell's effect",expectedPosition,actualPosition);

			Method isShielded = Class.forName(monsterPath).getDeclaredMethod("isShielded");
			Object shield = isShielded.invoke(dasherMonster);

			assertFalse("A shielded monster that lands on a contamination sock cell should have its shield removed",(boolean)shield);

			Object energyAfterInvoke = getEnergy.invoke(dasherMonster);

			assertEquals("If a shielded monster lands on a contamination sock cell, its energy should remain unchanged",energyBeforeInvoke,energyAfterInvoke);


		} catch (Exception e) {
			fail(e.getClass()+" "+e.getMessage());
		}
	}

	@Test(timeout=1000)
	public void testContaminationSockTransportMethodWithoutShield(){

		try {
			Object contaminationSock = createContaminationSock();

			Object dasherMonster = createDasher();

			int position = new Random().nextInt(5);
			int[] sockCellIndicies = {32,42,74,84,98};
			Method setPosition = Class.forName(monsterPath).getDeclaredMethod("setPosition", int.class);
			setPosition.invoke(dasherMonster, sockCellIndicies[position]);

			Method getEffect = Class.forName(transportCellPath).getDeclaredMethod("getEffect");
			Object effect = getEffect.invoke(contaminationSock);

			int expectedPosition = sockCellIndicies[position] + (int) effect;

			Method getEnergy = Class.forName(monsterPath).getDeclaredMethod("getEnergy");
			Object energyBeforeInvoke = getEnergy.invoke(dasherMonster);

			Field slipPenalty = Class.forName(constantsPath).getDeclaredField("SLIP_PENALTY");
			slipPenalty.setAccessible(true);
			int slipPenaltyValue = slipPenalty.getInt(slipPenalty);
			int expectedEnergyAfterInvoke = (int)energyBeforeInvoke - slipPenaltyValue;
			if(expectedEnergyAfterInvoke<0)
				expectedEnergyAfterInvoke = 0;

			Method transport = Class.forName(contaminationSockPath).getDeclaredMethod("transport", Class.forName(monsterPath));
			transport.invoke(contaminationSock, dasherMonster);

			Method getPosition = Class.forName(monsterPath).getDeclaredMethod("getPosition");
			Object actualPosition = getPosition.invoke(dasherMonster);

			assertEquals("Landing on a contamination sock cell should cause the monster to slip back a number of steps equal to the cell's effect",expectedPosition,actualPosition);

			Method isShielded = Class.forName(monsterPath).getDeclaredMethod("isShielded");
			Object shield = isShielded.invoke(dasherMonster);

			assertFalse("An unshielded monster landing on a contamination sock cell should remain unshielded",(boolean)shield);

			Object energyAfterInvoke = getEnergy.invoke(dasherMonster);

			assertEquals("If an unshielded monster lands on a contamination sock cell, its energy should be reduced by the cell's slip penalty",expectedEnergyAfterInvoke,energyAfterInvoke);


		} catch (Exception e) {
			fail(e.getClass()+" "+e.getMessage());
		}
	}


	@Test(timeout=1000)
	public void testCardPerformActionMethodAbstract(){
		Class cardClass;
		try {
			cardClass = Class.forName(cardPath);
			Method performAction = cardClass.getDeclaredMethod("performAction", Class.forName(monsterPath),Class.forName(monsterPath));
			assertTrue("performAction method should be an abstract method in card class.", Modifier.isAbstract(performAction.getModifiers()));
			assertTrue("performAction method in Card class should be a public one.",Modifier.isPublic(performAction.getModifiers()));

		} catch (ClassNotFoundException e) {
			fail("The card class not found");
		}catch (NoSuchMethodException e) {

			fail("The card class does not contain performAction method which takes 2 monster as parameters");

		} catch (SecurityException e) {

			 fail(e.getClass() + " " + e.getMessage());

		}
	}


	@Test(timeout=1000)
	public void testEnergyStealCardPerformActionMethodExists(){
       try {

			Class energyStealCard = Class.forName(energyStealCardPath);
			Method performActionMethod = energyStealCard.getDeclaredMethod("performAction", Class.forName(monsterPath),Class.forName(monsterPath));
			assertEquals("performAction method in energyStealCard class should be a void method", void.class, performActionMethod.getReturnType());
			assertTrue("performAction method in EnergyStealCard class should be a public one.",Modifier.isPublic(performActionMethod.getModifiers()));


		} catch(ClassNotFoundException e){

			fail("The energyStealCard class not found");

		} catch (NoSuchMethodException e) {

			fail("The energyStealCard class does not contain performAction method which takes 2 monster as parameters");

		} catch (SecurityException e) {

			 fail(e.getClass() + " " + e.getMessage());

		}
	}

	@Test(timeout=1000)
	public void testEnergyStealCardPerformActionOpponentShielded(){

		try {
			Object energyStealCard = createEnergyStealCard();

			Object player = createDynamo();

			Object opponent = createDynamo();

			Method setShielded = Class.forName(monsterPath).getDeclaredMethod("setShielded", boolean.class);
			setShielded.invoke(opponent, true);

			Method getEnergyMonster = Class.forName(monsterPath).getDeclaredMethod("getEnergy");
			Object opponentEnergy = getEnergyMonster.invoke(opponent);
			Object playerEnergy = getEnergyMonster.invoke(player);

			Method performAction = Class.forName(energyStealCardPath).getDeclaredMethod("performAction", Class.forName(monsterPath), Class.forName(monsterPath));
			performAction.invoke(energyStealCard, player, opponent);

			Method isShielded = Class.forName(monsterPath).getDeclaredMethod("isShielded");
			Object opponentShieldValue = isShielded.invoke(opponent);

			Object opponentEnergyAfterInvoke = getEnergyMonster.invoke(opponent);
			Object playerEnergyAfterInvoke = getEnergyMonster.invoke(player);

			assertFalse("A shielded opponent that blocks an energy steal attempt should have its shield consumed",(boolean)opponentShieldValue);
			assertEquals("If the opponent is shielded, the energy steal attempt should be blocked and have no effect",opponentEnergy,opponentEnergyAfterInvoke);
			assertEquals("If the opponent is shielded, the energy steal attempt should be blocked and have no effect",playerEnergy,playerEnergyAfterInvoke);


		} catch (Exception e) {
			fail(e.getClass()+" "+e.getMessage());
		}
	}

	@Test(timeout=1000)
	public void testEnergyStealCardPerformActionOpponentNotShielded(){

		try {
			Object energyStealCard = createEnergyStealCard();

			Object player = createMultiTasker();

			Object opponent = createDynamo();

			Method setShielded = Class.forName(monsterPath).getDeclaredMethod("setShielded", boolean.class);
			setShielded.invoke(opponent, false);

			Method getEnergyMonster = Class.forName(monsterPath).getDeclaredMethod("getEnergy");
			Method getEnergyCard = Class.forName(energyStealCardPath).getDeclaredMethod("getEnergy");
			Object opponentEnergy = getEnergyMonster.invoke(opponent);
			Object playerEnergy = getEnergyMonster.invoke(player);

			Object cardEnergy = getEnergyCard.invoke(energyStealCard);
			int energyToSteal = Math.min((int)opponentEnergy, (int)cardEnergy);

			int expectedOpponentEnergy = (int)opponentEnergy - energyToSteal*2;
			if(expectedOpponentEnergy<0)
				expectedOpponentEnergy=0;
			int expectedPlayerEnergy = (int)playerEnergy + energyToSteal + 200;

			Method performAction = Class.forName(energyStealCardPath).getDeclaredMethod("performAction", Class.forName(monsterPath), Class.forName(monsterPath));
			performAction.invoke(energyStealCard, player, opponent);

			Method isShielded = Class.forName(monsterPath).getDeclaredMethod("isShielded");
			Object opponentShieldValue = isShielded.invoke(opponent);

			Object opponentEnergyAfterInvoke = getEnergyMonster.invoke(opponent);
			Object playerEnergyAfterInvoke = getEnergyMonster.invoke(player);

			assertFalse("An unshielded opponent facing an energy steal attempt should have its shield status remain unchanged",(boolean)opponentShieldValue);
			assertEquals("If the opponent is not shielded, its energy should be reduced by the energy steal amount after the attempt",expectedOpponentEnergy,opponentEnergyAfterInvoke);
			assertEquals("If the opponent is not shielded, the player's energy should increase by the amount of energy stolen from the opponent",expectedPlayerEnergy,playerEnergyAfterInvoke);


		} catch (Exception e) {
			fail(e.getClass()+" "+e.getMessage());
		}
	}


	@Test(timeout=1000)
	public void testShieldCardPerformActionMethodExists(){
       try {

			Class shieldCard = Class.forName(shieldCardPath);
			Method performActionMethod = shieldCard.getDeclaredMethod("performAction", Class.forName(monsterPath),Class.forName(monsterPath));
			assertEquals("performAction method in shieldCard class should be a void method", void.class, performActionMethod.getReturnType());
			assertTrue("performAction method in ShieldCard class should be a public one.",Modifier.isPublic(performActionMethod.getModifiers()));


		} catch(ClassNotFoundException e){

			fail("The shieldCard class not found");

		} catch (NoSuchMethodException e) {

			fail("The shieldCard class does not contain performAction method which takes 2 monster as parameters");

		} catch (SecurityException e) {

			 fail(e.getClass() + " " + e.getMessage());

		}
	}

	@Test(timeout=1000)
	public void testShieldCardPerformActionMethodLogic(){

		try {
			Object shieldCard = createShieldCard();

			Object player = createDasher();

			Object opponent = createDynamo();

			Method setShielded = Class.forName(monsterPath).getDeclaredMethod("setShielded", boolean.class);
			setShielded.invoke(player, false);
			setShielded.invoke(opponent, true);

			Method performAction = Class.forName(shieldCardPath).getDeclaredMethod("performAction",Class.forName(monsterPath),Class.forName(monsterPath));
			performAction.invoke(shieldCard, player, opponent);

			Method isShielded = Class.forName(monsterPath).getDeclaredMethod("isShielded");
			Object playerShieldValue = isShielded.invoke(player);
			Object opponentShieldValue = isShielded.invoke(opponent);

			assertTrue("If the opponent is shielded, the shield card should transfer the opponent's shield to the player",(boolean)playerShieldValue);
			assertFalse("If the opponent is shielded, the shield card should transfer the opponent's shield to the player",(boolean)opponentShieldValue);

		} catch (Exception e) {
			 fail(e.getClass() + " " + e.getMessage());
		}

	}



	@Test(timeout=1000)
	public void testGameCheckWinConditionMethodExists(){
       try {

			Class game = Class.forName(gamePath);
			Method checkWinCondition = game.getDeclaredMethod("checkWinCondition", Class.forName(monsterPath));
			assertEquals("checkWinCondition method in game class should return boolean", boolean.class, checkWinCondition.getReturnType());
			assertTrue("checkWinCondition method in Game class should be a private one.",Modifier.isPrivate(checkWinCondition.getModifiers()));


		} catch(ClassNotFoundException e){

			fail("The game class not found");

		} catch (NoSuchMethodException e) {

			fail("The game class does not contain checkWinCondition method which takes a monster as a parameter");

		} catch (SecurityException e) {

			 fail(e.getClass() + " " + e.getMessage());

		}
	}

	@Test(timeout=1000)
	public void testGameCheckWinConditionNotAWinFirstCase(){

		try {
			Object game = createGameForTesting();

			Method getCurrent = Class.forName(gamePath).getDeclaredMethod("getCurrent");
			Object currentPlayer = getCurrent.invoke(game);

			Method checkWinCondition = Class.forName(gamePath).getDeclaredMethod("checkWinCondition", Class.forName(monsterPath));
			checkWinCondition.setAccessible(true);
			Object expectedOutput = checkWinCondition.invoke(game, currentPlayer);

			assertEquals("The player did not met the winning conditions yet",false,(boolean)expectedOutput);

		} catch (Exception e) {
			 fail(e.getClass() + " " + e.getMessage());
		}
	}

	@Test(timeout=1000)
	public void testGameCheckWinConditionNotAWinThirdCase(){

		try {
			Object game = createGameForTesting();

			Method getCurrent = Class.forName(gamePath).getDeclaredMethod("getCurrent");
			Object currentPlayer = getCurrent.invoke(game);

			Method setEnergy = Class.forName(monsterPath).getDeclaredMethod("setEnergy", int.class);
			int energy = new Random().nextInt(1000)+1000;
			setEnergy.invoke(currentPlayer, energy);

			Method checkWinCondition = Class.forName(gamePath).getDeclaredMethod("checkWinCondition", Class.forName(monsterPath));
			checkWinCondition.setAccessible(true);
			Object expectedOutput = checkWinCondition.invoke(game, currentPlayer);

			assertEquals("The player did not met all winning conditions yet",false,(boolean)expectedOutput);

		} catch (Exception e) {
			 fail(e.getClass() + " " + e.getMessage());
		}

	}

	@Test(timeout=1000)
	public void testGameCheckWinConditionAWin(){

		try {
			Object game = createGameForTesting();

			Method getCurrent = Class.forName(gamePath).getDeclaredMethod("getCurrent");
			Object currentPlayer = getCurrent.invoke(game);

			Method setPosition = Class.forName(monsterPath).getDeclaredMethod("setPosition", int.class);
			setPosition.invoke(currentPlayer, 99);

			Method setEnergy = Class.forName(monsterPath).getDeclaredMethod("setEnergy", int.class);
			int energy = new Random().nextInt(1000)+1000;
			setEnergy.invoke(currentPlayer, energy);

			Method checkWinCondition = Class.forName(gamePath).getDeclaredMethod("checkWinCondition", Class.forName(monsterPath));
			checkWinCondition.setAccessible(true);
			Object expectedOutput = checkWinCondition.invoke(game, currentPlayer);

			assertEquals("The player met all winning conditions",true,(boolean)expectedOutput);

		} catch (Exception e) {
			 fail(e.getClass() + " " + e.getMessage());
		}

	}


	@Test(timeout=1000)
	public void testGameGetWinnerMethodExists(){
       try {

			Class game = Class.forName(gamePath);
			Method getWinner = game.getDeclaredMethod("getWinner");
			assertEquals("getWinner method in game class should return monster", Class.forName(monsterPath), getWinner.getReturnType());
			assertTrue("getWinner method in Game class should be a public one.",Modifier.isPublic(getWinner.getModifiers()));


		} catch(ClassNotFoundException e){

			fail("The game class not found");

		} catch (NoSuchMethodException e) {

			fail("The game class does not contain getWinner method which takes 0 parameters");

		} catch (SecurityException e) {

			 fail(e.getClass() + " " + e.getMessage());

		}
	}

	@Test(timeout=1000)
	public void testGameGetWinnerNoWinner(){

		try {
			Object game = createGameForTesting();

			Method getWinner = Class.forName(gamePath).getDeclaredMethod("getWinner");
			Object expectedOutput = getWinner.invoke(game);

			assertEquals("No one of the players met the winning conditions yet",null,expectedOutput);

		} catch (Exception e) {
			 fail(e.getClass() + " " + e.getMessage());
		}
	}

	@Test(timeout=1000)
	public void testGameGetWinnerPlayerIsWinner(){

		try {
			Object game = createGameForTesting();

			Method getWinner = Class.forName(gamePath).getDeclaredMethod("getWinner");


			Method getPlayer = Class.forName(gamePath).getDeclaredMethod("getPlayer");
			Object player = getPlayer.invoke(game);

			Method setPosition = Class.forName(monsterPath).getDeclaredMethod("setPosition", int.class);
			setPosition.invoke(player, 99);

			Method setEnergy = Class.forName(monsterPath).getDeclaredMethod("setEnergy", int.class);
			int energy = new Random().nextInt(1000)+1000;
			setEnergy.invoke(player, energy);

			Object expectedOutput = getWinner.invoke(game);

			Method getName = Class.forName(monsterPath).getDeclaredMethod("getName");
			Object name = getName.invoke(player);


			assertEquals(name+" won the game",player,expectedOutput);

		} catch (Exception e) {
			 fail(e.getClass() + " " + e.getMessage());
		}
	}

	@Test(timeout=1000)
	public void testGameGetWinnerOpponentIsWinner(){

		try {
			Object game = createGameForTesting();

			Method getWinner = Class.forName(gamePath).getDeclaredMethod("getWinner");


			Method getOpponent = Class.forName(gamePath).getDeclaredMethod("getOpponent");
			Object opponent = getOpponent.invoke(game);

			Method setPosition = Class.forName(monsterPath).getDeclaredMethod("setPosition", int.class);
			setPosition.invoke(opponent, 99);

			Method setEnergy = Class.forName(monsterPath).getDeclaredMethod("setEnergy", int.class);
			int energy = new Random().nextInt(1000)+1000;
			setEnergy.invoke(opponent, energy);

			Object expectedOutput = getWinner.invoke(game);

			Method getName = Class.forName(monsterPath).getDeclaredMethod("getName");
			Object name = getName.invoke(opponent);

			assertEquals(name+" won the game",opponent,expectedOutput);

		} catch (Exception e) {
			 fail(e.getClass() + " " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testIndexToRowColInBoardRowLogicInEvenCaseCheckRow() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("indexToRowCol",
					int.class);
			method.setAccessible(true);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			Random rand = new Random();
			int row = rand.nextInt(5) * 2;
			int col;
			if (row == 0) {
				col = rand.nextInt(9) + 1;
			} else {
				col = rand.nextInt(10);
			}

			int index = row * 10 + col;

			int expected_row = index / 10;

			int[] result = (int[]) method.invoke(board_object, index);

			assertEquals(
					"The row value of the output of method indextToRowCol in class Board isn't calculated correctly",
					expected_row, result[0]);
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

			fail("Error while testing indexToRowCol in Board class: "
					+ e.getMessage());
		}

	}

	@Test(timeout = 1000)
	public void testIndexToRowColInBoardRowLogicInEvenCaseCheckColumn() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("indexToRowCol",
					int.class);
			method.setAccessible(true);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			Random rand = new Random();
			int row = rand.nextInt(5) * 2;
			int col;
			if (row == 0) {
				col = rand.nextInt(9) + 1;
			} else {
				col = rand.nextInt(10);
			}

			int index = row * 10 + col;

			int expectedColumn = index % 10;

			int[] result = (int[]) method.invoke(board_object, index);

			assertEquals(
					"The column value of the output of method indextToRowCol in class Board isn't calculated correctly",
					expectedColumn, result[1]);
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

			fail("Error while testing indexToRowCol in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testIndexToRowColInBoardRowLogicInOddCaseCheckRow() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("indexToRowCol",
					int.class);
			method.setAccessible(true);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			Random rand = new Random();
			int row = rand.nextInt(5) * 2 + 1;

			int col;
			if (row == 9) {
				col = rand.nextInt(9);
			} else {
				col = rand.nextInt(10);
			}

			int index = row * 10 + col;

			int expected_row = index / 10;

			int[] result = (int[]) method.invoke(board_object, index);

			assertEquals(
					"The row value of the output of method indextToRowCol in class Board isn't calculated correctly",
					expected_row, result[0]);
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

			fail("Error while testing indexToRowCol in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testIndexToRowColInBoardLogicInZeroCaseCheckRow() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("indexToRowCol",
					int.class);
			method.setAccessible(true);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			int index = 0;
			int[] result = (int[]) method.invoke(board_object, index);
			assertEquals(
					"The row value of the output of method indextToRowCol in class Board isn't calculated correctly",
					0, result[0]);
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

			fail("Error while testing indexToRowCol in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testIndexToRowColInBoardLogicIn99CaseCheckRow() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("indexToRowCol",
					int.class);
			method.setAccessible(true);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			int index = 99;
			int[] result = (int[]) method.invoke(board_object, index);
			assertEquals(
					"The row value of the output of method indextToRowCol in class Board isn't calculated correctly",
					9, result[0]);
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

			fail("Error while testing indexToRowCol in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testGetCellInBoardLogic() throws Exception {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("getCell", int.class);
			method.setAccessible(true);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			Random rand = new Random();
			int index = rand.nextInt(98) + 1;
			int row = index / 10;
			int col = index % 10;
			if (row % 2 == 1)
				col = 10 - 1 - col;

			String name = generateRandomString(5 + new Random().nextInt(11));
			Constructor<?> c = Class.forName(cellPath).getConstructor(
					String.class);
			Object cell = c.newInstance(name);
			Field field = board_class.getDeclaredField("boardCells");
			field.setAccessible(true);
			Object[][] boardCells = (Object[][]) field.get(board_object);
			boardCells[row][col] = cell;
			field.set(board_object, boardCells);
			Object returned_cell = method.invoke(board_object, index);

			assertSame(
					"getCell should return the cell stored at the correct zigzag position",
					cell, returned_cell);
		} catch (Exception e) {
			fail("Error while testing getCell in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testSetCellInBoardLogic() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("setCell", int.class,
					Class.forName(cellPath));
			method.setAccessible(true);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());
			Random rand = new Random();
			int index = rand.nextInt(100);
			int row = index / 10;
			int col = index % 10;
			if (row % 2 == 1)
				col = 10 - 1 - col;
			String name = generateRandomString(5 + new Random().nextInt(11));
			Constructor<?> c = Class.forName(cellPath).getConstructor(
					String.class);
			Object cell = c.newInstance(name);
			method.invoke(board_object, index, cell);
			Field field = board_class.getDeclaredField("boardCells");
			field.setAccessible(true);
			Object[][] boardCells = (Object[][]) field.get(board_object);
			Object the_cell_from_board = boardCells[row][col];
			assertSame(
					"setCell should set the correct cell in the correct position",
					cell, the_cell_from_board);
		} catch (Exception e) {
			fail("Error while testing setCell in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testInitializeBoardLogicEvenCellsPlacement() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("initializeBoard",
					ArrayList.class);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());


			Class<?> dataloader_class = Class.forName(dataLoaderPath);
			Method read_cells_method = dataloader_class
					.getDeclaredMethod("readCells");
			Constructor<?> data_loader_constructor = Class.forName(
					dataLoaderPath).getConstructor();
			Object dataloader = data_loader_constructor.newInstance();
			ArrayList<Object> cells = (ArrayList<Object>) read_cells_method
					.invoke(dataloader);

			method.invoke(board_object, cells);

			Field field = board_class.getDeclaredField("boardCells");
			field.setAccessible(true);
			Object[][] boardCells = (Object[][]) field.get(board_object);


			Class<?> constants_class = Class.forName(constantsPath);
			Field MONSTER_CELL_INDICES = constants_class
					.getDeclaredField("MONSTER_CELL_INDICES");
			int[] monster_cells_indices = (int[]) MONSTER_CELL_INDICES
					.get(null);
			Field CONVEYOR_CELL_INDICES = constants_class
					.getDeclaredField("CONVEYOR_CELL_INDICES");
			int[] conveyor_cells_indices = (int[]) CONVEYOR_CELL_INDICES
					.get(null);
			Field SOCK_CELL_INDICES = constants_class
					.getDeclaredField("SOCK_CELL_INDICES");
			int[] sock_cells_indices = (int[]) SOCK_CELL_INDICES.get(null);
			Field CARD_CELL_INDICES = constants_class
					.getDeclaredField("CARD_CELL_INDICES");
			int[] card_cells_indices = (int[]) CARD_CELL_INDICES.get(null);

			for (int i = 0; i < 100; i++) {

				if (i % 2 == 0 && !contains(monster_cells_indices, i)
						&& !contains(conveyor_cells_indices, i)
						&& !contains(sock_cells_indices, i)
						&& !contains(card_cells_indices, i)) {

					int row = i / 10;
					int col = i % 10;
					if (row % 2 == 1)
						col = 10 - 1 - col;
					Object current_cell = boardCells[row][col];
					assertNotNull("Cell at index " + i + " should not be null",
							current_cell);
					assertEquals("Index " + i
							+ " in the board should contain a normal Cell",
							Class.forName(cellPath), current_cell.getClass());
				}
			}
		} catch (Exception e) {
			fail("Error while testing initializeBoard in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testInitializeBoardLogicOddCellsContentPlacement() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("initializeBoard",
					ArrayList.class);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());


			Class<?> dataloader_class = Class.forName(dataLoaderPath);
			Method read_cells_method = dataloader_class
					.getDeclaredMethod("readCells");
			Constructor<?> data_loader_constructor = Class.forName(
					dataLoaderPath).getConstructor();
			Object dataloader = data_loader_constructor.newInstance();
			ArrayList<Object> cells = (ArrayList<Object>) read_cells_method
					.invoke(dataloader);
			ArrayList<Object> expectedDoors = new ArrayList<>();
			for (Object cell : cells) {
				if (Class.forName(doorCellPath).isInstance(cell)) {
					expectedDoors.add(cell);
				}
			}
			method.invoke(board_object, cells);

			Field field = board_class.getDeclaredField("boardCells");
			field.setAccessible(true);
			Object[][] boardCells = (Object[][]) field.get(board_object);


			Class<?> constants_class = Class.forName(constantsPath);
			Field MONSTER_CELL_INDICES = constants_class
					.getDeclaredField("MONSTER_CELL_INDICES");
			int[] monster_cells_indices = (int[]) MONSTER_CELL_INDICES
					.get(null);
			Field CONVEYOR_CELL_INDICES = constants_class
					.getDeclaredField("CONVEYOR_CELL_INDICES");
			int[] conveyor_cells_indices = (int[]) CONVEYOR_CELL_INDICES
					.get(null);
			Field SOCK_CELL_INDICES = constants_class
					.getDeclaredField("SOCK_CELL_INDICES");
			int[] sock_cells_indices = (int[]) SOCK_CELL_INDICES.get(null);
			Field CARD_CELL_INDICES = constants_class
					.getDeclaredField("CARD_CELL_INDICES");
			int[] card_cells_indices = (int[]) CARD_CELL_INDICES.get(null);

			for (int i = 0; i < 100; i++) {

				if (i % 2 == 1 && !contains(monster_cells_indices, i)
						&& !contains(conveyor_cells_indices, i)
						&& !contains(sock_cells_indices, i)
						&& !contains(card_cells_indices, i)) {

					int row = i / 10;
					int col = i % 10;
					if (row % 2 == 1)
						col = 10 - 1 - col;
					Object current_cell = boardCells[row][col];

					assertNotNull("Cell at index " + i + " should not be null",
							current_cell);

					int expectedDoorIndex = i / 2;
					assertSame(
							"Index "
									+ i
									+ " should contain the exact DoorCell originally assigned to that odd index",
							expectedDoors.get(expectedDoorIndex), current_cell);
				}
			}
		} catch (Exception e) {
			fail("Error while testing initializeBoard in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testInitializeBoardLogicCardCellsPlacement() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("initializeBoard",
					ArrayList.class);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());


			Class<?> dataloader_class = Class.forName(dataLoaderPath);
			Method read_cells_method = dataloader_class
					.getDeclaredMethod("readCells");
			Constructor<?> data_loader_constructor = Class.forName(
					dataLoaderPath).getConstructor();
			Object dataloader = data_loader_constructor.newInstance();
			ArrayList<Object> cells = (ArrayList<Object>) read_cells_method
					.invoke(dataloader);

			method.invoke(board_object, cells);

			Field field = board_class.getDeclaredField("boardCells");
			field.setAccessible(true);
			Object[][] boardCells = (Object[][]) field.get(board_object);


			Class<?> constants_class = Class.forName(constantsPath);
			Field CARD_CELL_INDICES = constants_class
					.getDeclaredField("CARD_CELL_INDICES");
			int[] card_cells_indices = (int[]) CARD_CELL_INDICES.get(null);

			for (int i = 0; i < card_cells_indices.length; i++) {
				int index = card_cells_indices[i];

				int row = index / 10;
				int col = index % 10;
				if (row % 2 == 1)
					col = 10 - 1 - col;
				Object current_cell = boardCells[row][col];

				assertNotNull("Cell at index " + index + " should not be null",
						current_cell);
				assertEquals("Index " + index
						+ " in the board should contain a card Cell",
						Class.forName(cardCellPath), current_cell.getClass());
			}
		} catch (Exception e) {
			fail("Error while testing initializeBoard in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testInitializeBoardLogicConveyorTypePlacement() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("initializeBoard",
					ArrayList.class);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());


			Class<?> dataloader_class = Class.forName(dataLoaderPath);
			Method read_cells_method = dataloader_class
					.getDeclaredMethod("readCells");
			Constructor<?> data_loader_constructor = Class.forName(
					dataLoaderPath).getConstructor();
			Object dataloader = data_loader_constructor.newInstance();
			ArrayList<Object> cells = (ArrayList<Object>) read_cells_method
					.invoke(dataloader);
			method.invoke(board_object, cells);

			Field field = board_class.getDeclaredField("boardCells");
			field.setAccessible(true);
			Object[][] boardCells = (Object[][]) field.get(board_object);


			Class<?> constants_class = Class.forName(constantsPath);
			Field CONVEYOR_CELL_INDICES = constants_class
					.getDeclaredField("CONVEYOR_CELL_INDICES");
			int[] conveyor_cells_indices = (int[]) CONVEYOR_CELL_INDICES
					.get(null);

			for (int i = 0; i < 100; i++) {

				if (contains(conveyor_cells_indices, i)) {

					int row = i / 10;
					int col = i % 10;
					if (row % 2 == 1)
						col = 10 - 1 - col;
					Object current_cell = boardCells[row][col];

					assertNotNull("Cell at index " + i + " should not be null",
							current_cell);
					assertEquals(
							"Index "
									+ i
									+ " in the board should contain a conveyorBelt cell",
							Class.forName(conveyorBeltPath),
							current_cell.getClass());
				}
			}
		} catch (Exception e) {
			fail("Error while testing initializeBoard in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testInitializeBoardLogicSockCellsTypePlacement() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("initializeBoard",
					ArrayList.class);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());


			Class<?> dataloader_class = Class.forName(dataLoaderPath);
			Method read_cells_method = dataloader_class
					.getDeclaredMethod("readCells");
			Constructor<?> data_loader_constructor = Class.forName(
					dataLoaderPath).getConstructor();
			Object dataloader = data_loader_constructor.newInstance();
			ArrayList<Object> cells = (ArrayList<Object>) read_cells_method
					.invoke(dataloader);
			method.invoke(board_object, cells);

			Field field = board_class.getDeclaredField("boardCells");
			field.setAccessible(true);
			Object[][] boardCells = (Object[][]) field.get(board_object);


			Class<?> constants_class = Class.forName(constantsPath);
			Field SOCK_CELL_INDICES = constants_class
					.getDeclaredField("SOCK_CELL_INDICES");
			int[] sock_cells_indices = (int[]) SOCK_CELL_INDICES.get(null);

			for (int j = 0; j < sock_cells_indices.length; j++) {

				int index = sock_cells_indices[j];

				int row = index / 10;
				int col = index % 10;
				if (row % 2 == 1)
					col = 10 - 1 - col;
				Object current_cell = boardCells[row][col];

				assertNotNull("Cell at index " + index + " should not be null",
						current_cell);
				assertEquals("Index " + index
						+ " in the board should contain a ContaminationSock",
						Class.forName(contaminationSockPath),
						current_cell.getClass());
			}
		} catch (Exception e) {
			fail("Error while testing initializeBoard in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testInitializeBoardLogicMonsterCellsPlacement() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("initializeBoard",
					ArrayList.class);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			Class<?> dataloader_class = Class.forName(dataLoaderPath);
			Method read_cells_method = dataloader_class
					.getDeclaredMethod("readCells");
			Constructor<?> data_loader_constructor = Class.forName(
					dataLoaderPath).getConstructor();
			Object dataloader = data_loader_constructor.newInstance();
			ArrayList<Object> cells = (ArrayList<Object>) read_cells_method
					.invoke(dataloader);

			Field stationed_monsters = board_class
					.getDeclaredField("stationedMonsters");
			stationed_monsters.setAccessible(true);
			ArrayList<Object> stationed_monsters_array = (ArrayList<Object>) stationed_monsters
					.get(null);
			stationed_monsters_array.clear();

			Random rand = new Random();
			int[] count = new int[5];
			int i = 0;
			while (i < 6) {
				int type = rand.nextInt(4) + 1;

				if (count[type] < 2) {

					Object monster = null;

					switch (type) {
					case 1:
						monster = createDasher();
						break;
					case 2:
						monster = createDynamo();
						break;
					case 3:
						monster = createMultiTasker();
						break;
					case 4:
						monster = createSchemer();
						break;
					}

					stationed_monsters_array.add(monster);
					count[type]++;

					i++;
				}
			}
			stationed_monsters.set(null, stationed_monsters_array);

			method.invoke(board_object, cells);

			Field field = board_class.getDeclaredField("boardCells");
			field.setAccessible(true);
			Object[][] boardCells = (Object[][]) field.get(board_object);

			Class<?> constants_class = Class.forName(constantsPath);
			Field MONSTER_CELL_INDICES = constants_class
					.getDeclaredField("MONSTER_CELL_INDICES");
			int[] monster_cells_indices = (int[]) MONSTER_CELL_INDICES
					.get(null);


			for (int j = 0; j < monster_cells_indices.length; j++) {
				int index = monster_cells_indices[j];
				int row = index / 10;
				int col = index % 10;
				if (row % 2 == 1)
					col = 10 - 1 - col;
				Object current_cell = boardCells[row][col];
				assertNotNull("Cell at index " + index + " should not be null",
						current_cell);
				assertEquals("Index " + index
						+ " in the board should contain a monster Cell",
						Class.forName(monsterCellPath), current_cell.getClass());
			}
		} catch (Exception e) {
			fail("Error while testing initializeBoard in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testInitializeBoardLogicMonsterCellsName() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("initializeBoard",
					ArrayList.class);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			Class<?> dataloader_class = Class.forName(dataLoaderPath);
			Method read_cells_method = dataloader_class
					.getDeclaredMethod("readCells");
			Constructor<?> data_loader_constructor = Class.forName(
					dataLoaderPath).getConstructor();
			Object dataloader = data_loader_constructor.newInstance();
			ArrayList<Object> cells = (ArrayList<Object>) read_cells_method
					.invoke(dataloader);

			Field stationed_monsters = board_class
					.getDeclaredField("stationedMonsters");
			stationed_monsters.setAccessible(true);
			ArrayList<Object> stationed_monsters_array = (ArrayList<Object>) stationed_monsters
					.get(null);
			stationed_monsters_array.clear();

			Random rand = new Random();
			int[] count = new int[5];
			int i = 0;
			while (i < 6) {
				int type = rand.nextInt(4) + 1;

				if (count[type] < 2) {

					Object monster = null;

					switch (type) {
					case 1:
						monster = createDasher();
						break;
					case 2:
						monster = createDynamo();
						break;
					case 3:
						monster = createMultiTasker();
						break;
					case 4:
						monster = createSchemer();
						break;
					}

					stationed_monsters_array.add(monster);
					count[type]++;

					i++;
				}
			}
			stationed_monsters.set(null, stationed_monsters_array);

			method.invoke(board_object, cells);

			Field field = board_class.getDeclaredField("boardCells");
			field.setAccessible(true);
			Object[][] boardCells = (Object[][]) field.get(board_object);

			Class<?> constants_class = Class.forName(constantsPath);
			Field MONSTER_CELL_INDICES = constants_class
					.getDeclaredField("MONSTER_CELL_INDICES");
			int[] monster_cells_indices = (int[]) MONSTER_CELL_INDICES
					.get(null);

			for (int j = 0; j < monster_cells_indices.length; j++) {
				int index = monster_cells_indices[j];
				int row = index / 10;
				int col = index % 10;
				if (row % 2 == 1)
					col = 10 - 1 - col;
				Object current_cell = boardCells[row][col];
				Object curent_monster = stationed_monsters_array.get(j);
				Field monster_name_field = Class.forName(monsterPath)
						.getDeclaredField("name");
				monster_name_field.setAccessible(true);
				String monster_name = (String) monster_name_field
						.get(curent_monster);
				Field cell_name_field = Class.forName(cellPath)
						.getDeclaredField("name");
				cell_name_field.setAccessible(true);
				String cell_name = (String) cell_name_field.get(current_cell);
				assertNotNull("Cell at index " + index + " should not be null",
						current_cell);
				assertEquals("MonsterCell at index " + index
						+ " in the board doesn't has the correct name",
						monster_name, cell_name);
			}
		} catch (Exception e) {
			fail("Error while testing initializeBoard in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testInitializeBoardLogicSetCorrectPoistionForEachMonster() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("initializeBoard",
					ArrayList.class);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());


			Class<?> dataloader_class = Class.forName(dataLoaderPath);
			Method read_cells_method = dataloader_class
					.getDeclaredMethod("readCells");
			Constructor<?> data_loader_constructor = Class.forName(
					dataLoaderPath).getConstructor();
			Object dataloader = data_loader_constructor.newInstance();
			ArrayList<Object> cells = (ArrayList<Object>) read_cells_method
					.invoke(dataloader);

			Field stationed_monsters = board_class
					.getDeclaredField("stationedMonsters");
			stationed_monsters.setAccessible(true);
			ArrayList<Object> stationed_monsters_array = (ArrayList<Object>) stationed_monsters
					.get(null);

			stationed_monsters_array.clear();

			Random rand = new Random();
			int[] count = new int[5];
			int i = 0;
			while (i < 6) {
				int type = rand.nextInt(4) + 1;

				if (count[type] < 2) {

					Object monster = null;

					switch (type) {
					case 1:
						monster = createDasher();
						break;
					case 2:
						monster = createDynamo();
						break;
					case 3:
						monster = createMultiTasker();
						break;
					case 4:
						monster = createSchemer();
						break;
					}

					stationed_monsters_array.add(monster);
					count[type]++;

					i++;
				}
			}
			stationed_monsters.set(null, stationed_monsters_array);

			method.invoke(board_object, cells);

			Class<?> constants_class = Class.forName(constantsPath);
			Field MONSTER_CELL_INDICES = constants_class
					.getDeclaredField("MONSTER_CELL_INDICES");
			int[] monsters_cells_indices = (int[]) MONSTER_CELL_INDICES
					.get(null);

			for (int j = 0; j < stationed_monsters_array.size(); j++) {
				Object monster = stationed_monsters_array.get(j);
				int expectedIndex = monsters_cells_indices[j];
				Field monster_position = Class.forName(monsterPath)
						.getDeclaredField("position");
				monster_position.setAccessible(true);
				int actualPosition = (int) monster_position.get(monster);
				assertEquals("Monster at index " + j + " has wrong position",
						expectedIndex, actualPosition);
			}
		} catch (Exception e) {
			fail("Error while testing initializeBoard in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testUpdateMonsterPositionsInClassBoardCheckTheBoard() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod(
					"updateMonsterPositions", Class.forName(monsterPath),
					Class.forName(monsterPath));
			method.setAccessible(true);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());
			Field boardCells_field = board_class.getDeclaredField("boardCells");
			boardCells_field.setAccessible(true);
			Object[][] boardCells = (Object[][]) boardCells_field
					.get(board_object);

			Constructor<?> cell_constructor = Class.forName(cellPath)
					.getConstructor(String.class);

			for (int row = 0; row < 10; row++)
				for (int col = 0; col < 10; col++)
					boardCells[row][col] = cell_constructor.newInstance("cell");
			boardCells_field.set(board_object, boardCells);
			Field monsterField = Class.forName(cellPath).getDeclaredField(
					"monster");
			monsterField.setAccessible(true);
			Object[] monsters = { createDasher(), createDynamo() };
			Random rand = new Random();
			int numberOfCellsToDirty = rand.nextInt(10) + 1;
			int randomIndex = 0;
			for (int i = 0; i < numberOfCellsToDirty; i++) {
				randomIndex = randomIndex = rand.nextInt(98) + 2;
				;
				int row = randomIndex / 10;
				int col = randomIndex % 10;
				if (row % 2 == 1)
					col = 10 - 1 - col;

				Object randomMonster = monsters[rand.nextInt(monsters.length)];
				monsterField.set(boardCells[row][col], randomMonster);
			}
			Object player = createDasher();
			Object opponent = createSchemer();
			int playerPosition, opponentPosition;
			playerPosition = 1;
			opponentPosition = 0;
			Field positionField = Class.forName(monsterPath).getDeclaredField(
					"position");
			positionField.setAccessible(true);
			positionField.set(player, playerPosition);
			positionField.set(opponent, opponentPosition);
			method.invoke(board_object, player, opponent);
			for (int i = 0; i < 100; i++) {
				if (i != playerPosition && i != opponentPosition) {
					int row = i / 10;
					int col = i % 10;
					if (row % 2 == 1)
						col = 10 - 1 - col;
					Object cell = boardCells[row][col];
					Object monsterInCell = monsterField.get(cell);
					assertNull("Cell at index " + i
							+ " should be null but has a monster",
							monsterInCell);
				}
			}
		} catch (Exception e) {
			fail("Error while testing updateMonsterPositions in board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testUpdateMonsterPositionsInClassBoardCheckPlayer() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod(
					"updateMonsterPositions", Class.forName(monsterPath),
					Class.forName(monsterPath));
			method.setAccessible(true);
			Constructor<?> board_constructor = Class.forName(boardPath)
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());
			Field boardCells_field = board_class.getDeclaredField("boardCells");
			boardCells_field.setAccessible(true);
			Object[][] boardCells = (Object[][]) boardCells_field
					.get(board_object);

			Constructor<?> cell_constructor = Class.forName(cellPath)
					.getConstructor(String.class);

			for (int row = 0; row < 10; row++)
				for (int col = 0; col < 10; col++)
					boardCells[row][col] = cell_constructor.newInstance("cell");
			boardCells_field.set(board_object, boardCells);
			Object player = createDasher();
			Object opponent = createSchemer();
			Random rand = new Random();
			int playerPosition, opponentPosition;
			playerPosition = rand.nextInt(99) + 1;
			opponentPosition = 0;

			Field positionField = Class.forName(monsterPath).getDeclaredField(
					"position");
			positionField.setAccessible(true);
			positionField.set(player, playerPosition);
			positionField.set(opponent, opponentPosition);
			method.invoke(board_object, player, opponent);


			int row = playerPosition / 10;
			int col = playerPosition % 10;
			if (row % 2 == 1)
				col = 10 - 1 - col;
			Object expected_cell = boardCells[row][col];

			Field montser_cell_field = Class.forName(cellPath)
					.getDeclaredField("monster");
			montser_cell_field.setAccessible(true);
			Object expected_monster_in_expected_cell = montser_cell_field
					.get(expected_cell);

			assertSame(
					"After updateMonsterPositions is called, player should be placed at cell index "
							+ playerPosition + " but was not", player,
					expected_monster_in_expected_cell);
		} catch (Exception e) {
			fail("Error while testing updateMonsterPositions in Board class: "
					+ e.getMessage());
		}

	}

	@Test(timeout = 1000)
	public void testMoveMonsterInClassBoardNormalMove() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("moveMonster",
					Class.forName(monsterPath), int.class,
					Class.forName(monsterPath));
			method.setAccessible(true);

			Constructor<?> board_constructor = board_class
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			Field boardCells_field = board_class.getDeclaredField("boardCells");
			boardCells_field.setAccessible(true);
			Object[][] boardCells = (Object[][]) boardCells_field
					.get(board_object);
			Constructor<?> cell_constructor = Class.forName(cellPath)
					.getConstructor(String.class);
			for (int row = 0; row < 10; row++)
				for (int col = 0; col < 10; col++)
					boardCells[row][col] = cell_constructor.newInstance("cell");

			Object player = createSchemer();
			Object opponent = createDynamo();

			Random rand = new Random();
			int roll = rand.nextInt(6) + 1;
			int playerPosition = rand.nextInt(87) + 1;
			int opponentPosition;
			do {
				opponentPosition = rand.nextInt(100);
			} while (opponentPosition == playerPosition + roll);

			Field positionField = Class.forName(monsterPath).getDeclaredField(
					"position");
			positionField.setAccessible(true);
			positionField.set(player, playerPosition);
			positionField.set(opponent, opponentPosition);

			method.invoke(board_object, player, roll, opponent);

			int expected_position = playerPosition + roll;

			int actual_position = (int) positionField.get(player);

			assertEquals("After moveMonster is called, player was at "
					+ playerPosition + " with roll " + roll
					+ " so should be at ", expected_position, actual_position);
		} catch (Exception e) {
			fail("Error while testing moveMonster in Board class: "
					+ e.getMessage());
		}

	}

	@Test(timeout = 1000)
	public void testMoveMonsterInClassBoardOnLandCalled() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("moveMonster",
					Class.forName(monsterPath), int.class,
					Class.forName(monsterPath));
			method.setAccessible(true);

			Constructor<?> board_constructor = board_class
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			Field boardCells_field = board_class.getDeclaredField("boardCells");
			boardCells_field.setAccessible(true);
			Object[][] boardCells = (Object[][]) boardCells_field
					.get(board_object);
			Constructor<?> cell_constructor = Class.forName(cellPath)
					.getConstructor(String.class);
			for (int row = 0; row < 10; row++)
				for (int col = 0; col < 10; col++)
					boardCells[row][col] = cell_constructor.newInstance("cell");

			Object player = createSchemer();
			Object opponent = createDynamo();

			Random rand = new Random();
			int roll = rand.nextInt(6) + 1;
			int playerPosition = rand.nextInt(87) + 1;
			int opponentPosition;
			do {
				opponentPosition = rand.nextInt(100);
			} while (opponentPosition == playerPosition + roll);

			Field positionField = Class.forName(monsterPath).getDeclaredField(
					"position");
			positionField.setAccessible(true);
			positionField.set(player, playerPosition);
			positionField.set(opponent, opponentPosition);

			method.invoke(board_object, player, roll, opponent);

			int expectedPosition = playerPosition + roll;
			int row = expectedPosition / 10;
			int col = expectedPosition % 10;
			if (row % 2 == 1)
				col = 9 - col;
			Object landedCell = boardCells[row][col];

			Field monsterField = Class.forName(cellPath).getDeclaredField(
					"monster");
			monsterField.setAccessible(true);
			Object monsterInCell = monsterField.get(landedCell);

			assertSame(
					"In moveMonster, onLand should have been called so cell at "
							+ expectedPosition
							+ " should hold the player monster", player,
					monsterInCell);
		} catch (Exception e) {
			fail("Error while testing moveMonster in board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveMonsterInClassBoardInvalidMovePosition() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("moveMonster",
					Class.forName(monsterPath), int.class,
					Class.forName(monsterPath));
			method.setAccessible(true);

			Constructor<?> board_constructor = board_class
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			Field boardCells_field = board_class.getDeclaredField("boardCells");
			boardCells_field.setAccessible(true);
			Object[][] boardCells = (Object[][]) boardCells_field
					.get(board_object);
			Constructor<?> cell_constructor = Class.forName(cellPath)
					.getConstructor(String.class);
			for (int row = 0; row < 10; row++)
				for (int col = 0; col < 10; col++)
					boardCells[row][col] = cell_constructor.newInstance("cell");

			Object player = createSchemer();
			Object opponent = createDynamo();

			Random rand = new Random();
			int roll = rand.nextInt(6) + 1;
			int opponentPosition = rand.nextInt(87) + 6;
			int playerPosition = opponentPosition - roll;

			Field positionField = Class.forName(monsterPath).getDeclaredField(
					"position");
			positionField.setAccessible(true);
			positionField.set(player, playerPosition);
			positionField.set(opponent, opponentPosition);

			try {
				method.invoke(board_object, player, roll, opponent);
			} catch (Exception e) {
			}

			int actual_position = (int) positionField.get(player);

			assertEquals(
					"After invalid move, player position should revert to oldPosition ",
					playerPosition, actual_position);
		} catch (Exception e) {
			fail("Error while testing moveMonster in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveMonsterInClassBoardConfusionDecrementCurrentMonster() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("moveMonster",
					Class.forName(monsterPath), int.class,
					Class.forName(monsterPath));
			method.setAccessible(true);

			Constructor<?> board_constructor = board_class
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			Field boardCells_field = board_class.getDeclaredField("boardCells");
			boardCells_field.setAccessible(true);
			Object[][] boardCells = (Object[][]) boardCells_field
					.get(board_object);
			Constructor<?> cell_constructor = Class.forName(cellPath)
					.getConstructor(String.class);
			for (int row = 0; row < 10; row++)
				for (int col = 0; col < 10; col++)
					boardCells[row][col] = cell_constructor.newInstance("cell");

			Object player = createSchemer();
			Object opponent = createDynamo();

			Random rand = new Random();
			int roll = rand.nextInt(6) + 1;
			int playerPosition = rand.nextInt(87) + 1;
			int opponentPosition;
			do {
				opponentPosition = rand.nextInt(100);
			} while (opponentPosition == playerPosition + roll);

			Field positionField = Class.forName(monsterPath).getDeclaredField(
					"position");
			positionField.setAccessible(true);
			positionField.set(player, playerPosition);
			positionField.set(opponent, opponentPosition);

			int confusionTurns = rand.nextInt(3) + 1;
			Field confusionTurnsField = Class.forName(monsterPath)
					.getDeclaredField("confusionTurns");
			confusionTurnsField.setAccessible(true);
			confusionTurnsField.set(player, confusionTurns);
			confusionTurnsField.set(opponent, confusionTurns);

			method.invoke(board_object, player, roll, opponent);

			int confusionTurnsAfter = (int) confusionTurnsField.get(player);

			assertEquals(
					"After moveMonster, currentMonster confusionTurns should be decremented from ",
					confusionTurns - 1, confusionTurnsAfter);
		} catch (Exception e) {
			fail("Error while testing moveMonster in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveMonsterInClassBoardNoConfusionOpponentMonster() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("moveMonster",
					Class.forName(monsterPath), int.class,
					Class.forName(monsterPath));
			method.setAccessible(true);

			Constructor<?> board_constructor = board_class
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			Field boardCells_field = board_class.getDeclaredField("boardCells");
			boardCells_field.setAccessible(true);
			Object[][] boardCells = (Object[][]) boardCells_field
					.get(board_object);
			Constructor<?> cell_constructor = Class.forName(cellPath)
					.getConstructor(String.class);
			for (int row = 0; row < 10; row++)
				for (int col = 0; col < 10; col++)
					boardCells[row][col] = cell_constructor.newInstance("cell");

			Object player = createSchemer();
			Object opponent = createDynamo();

			Random rand = new Random();
			int roll = rand.nextInt(6) + 1;
			int playerPosition = rand.nextInt(87) + 1;
			int opponentPosition;
			do {
				opponentPosition = rand.nextInt(100);
			} while (opponentPosition == playerPosition + roll);

			Field positionField = Class.forName(monsterPath).getDeclaredField(
					"position");
			positionField.setAccessible(true);
			positionField.set(player, playerPosition);
			positionField.set(opponent, opponentPosition);

			Random rand2 = new Random();
			int opponentConfusionTurns = rand2.nextInt(3) + 1;
			Field confusionTurnsField = Class.forName(monsterPath)
					.getDeclaredField("confusionTurns");
			confusionTurnsField.setAccessible(true);
			confusionTurnsField.set(player, 0);
			confusionTurnsField.set(opponent, opponentConfusionTurns);
			method.invoke(board_object, player, roll, opponent);

			int opponentConfusionTurnsAfter = (int) confusionTurnsField
					.get(opponent);
			assertEquals(
					"Player is not confused so opponent confusionTurns should stay ",
					opponentConfusionTurns, opponentConfusionTurnsAfter);
		} catch (Exception e) {
			fail("Error while testing moveMonster in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveMonsterInClassBoardUpdateMonsterPositionsCalling() {
		try {
			Class<?> board_class = Class.forName(boardPath);
			Method method = board_class.getDeclaredMethod("moveMonster",
					Class.forName(monsterPath), int.class,
					Class.forName(monsterPath));
			method.setAccessible(true);

			Constructor<?> board_constructor = board_class
					.getConstructor(ArrayList.class);
			Object board_object = board_constructor
					.newInstance(new ArrayList<>());

			Field boardCells_field = board_class.getDeclaredField("boardCells");
			boardCells_field.setAccessible(true);
			Object[][] boardCells = (Object[][]) boardCells_field
					.get(board_object);
			Constructor<?> cell_constructor = Class.forName(cellPath)
					.getConstructor(String.class);
			for (int row = 0; row < 10; row++)
				for (int col = 0; col < 10; col++)
					boardCells[row][col] = cell_constructor.newInstance("cell");

			Field monsterField = Class.forName(cellPath).getDeclaredField(
					"monster");
			monsterField.setAccessible(true);
			Object[] monsters = { createDasher(), createDynamo() };
			Random rand = new Random();
			int numberOfCellsToDirty = rand.nextInt(10) + 1;
			int randomIndex = 0;
			for (int i = 0; i < numberOfCellsToDirty; i++) {
				randomIndex = rand.nextInt(98) + 2;
				;
				int row = randomIndex / 10;
				int col = randomIndex % 10;
				if (row % 2 == 1)
					col = 10 - 1 - col;
				Object randomMonster = monsters[rand.nextInt(monsters.length)];
				monsterField.set(boardCells[row][col], randomMonster);
			}

			Object player = createSchemer();
			Object opponent = createDynamo();

			int roll = rand.nextInt(6) + 1;
			int playerPosition = rand.nextInt(87) + 1;
			int opponentPosition;
			do {
				opponentPosition = rand.nextInt(100);
			} while (opponentPosition == playerPosition + roll);

			Field positionField = Class.forName(monsterPath).getDeclaredField(
					"position");
			positionField.setAccessible(true);
			positionField.set(player, playerPosition);
			positionField.set(opponent, opponentPosition);

			method.invoke(board_object, player, roll, opponent);

			for (int i = 0; i < 100; i++) {
				if (i != (playerPosition + roll) && i != opponentPosition) {
					int row = i / 10;
					int col = i % 10;
					if (row % 2 == 1)
						col = 10 - 1 - col;
					Object cell = boardCells[row][col];
					Object monsterInCell = monsterField.get(cell);
					assertNull("After moveMonster, Cell at index " + i
							+ " should be null but has a monster",
							monsterInCell);
				}
			}
		} catch (Exception e) {
			fail("Error while testing moveMonster in Board class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveInClassMonsterCaseExactBoundary() {
		try {
			Class<?> monster_class = Class.forName(monsterPath);
			Method method = monster_class.getDeclaredMethod("move", int.class);
			Class<?> dynamo_class = Class.forName(dynamoPath);
			Object dynamo_object = createDynamo();

			Random rand = new Random();
			int distance = rand.nextInt(6) + 1;
			int basePosition = 100 - distance;
			int expected_position = 0;


			Field position = dynamo_class.getSuperclass().getDeclaredField(
					"position");
			position.setAccessible(true);
			position.set(dynamo_object, basePosition);

			method.invoke(dynamo_object, distance);


			int monster_position = (int) position.get(dynamo_object);

			assertEquals("After moving the monster from position "
					+ basePosition + " by distance " + distance
					+ " the new position should be", expected_position,
					monster_position);
		} catch (Exception e) {
			fail("Error while testing move in Monster class: " + e.getMessage());
		}

	}

	@Test(timeout = 1000)
	public void testMoveInClassMonsterCaseWrapAround() {
		try {
			Class<?> monster_class = Class.forName(monsterPath);
			Method method = monster_class.getDeclaredMethod("move", int.class);
			Class<?> dynamo_class = Class.forName(dynamoPath);
			Object dynamo_object = createDynamo();

			Random rand = new Random();

			int basePosition = rand.nextInt(5) + 95;
			int distance = rand.nextInt(6) + 1;
			while (basePosition + distance <= 100) {
				distance = rand.nextInt(6) + 1;
			}

			int expected_position = (basePosition + distance) % 100;

			Field position = dynamo_class.getSuperclass().getDeclaredField(
					"position");
			position.setAccessible(true);
			position.set(dynamo_object, basePosition);

			method.invoke(dynamo_object, distance);

			int monster_position = (int) position.get(dynamo_object);

			assertEquals("After moving the monster from position "
					+ basePosition + " by distance " + distance
					+ ", the new position should be " + expected_position,
					expected_position, monster_position);
		} catch (Exception e) {
			fail("Error while testing move in Monster class: " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveInClassMonsterCaseNormalCase() {
		try {
			Class<?> monster_class = Class.forName(monsterPath);
			Method method = monster_class.getDeclaredMethod("move", int.class);
			Class<?> dynamo_class = Class.forName(dynamoPath);
			Object dynamo_object = createDynamo();


			Random rand = new Random();
			int basePosition = rand.nextInt(93) + 1;
			int distance = rand.nextInt(6) + 1;
			int expected_position = (basePosition + distance) % 100;

			Field position = dynamo_class.getSuperclass().getDeclaredField(
					"position");
			position.setAccessible(true);
			position.set(dynamo_object, basePosition);


			method.invoke(dynamo_object, distance);


			int monster_position = (int) position.get(dynamo_object);

			assertEquals("After moving the monster from position "
					+ basePosition + " by distance " + distance
					+ " the new position should be", expected_position,
					monster_position);
		} catch (Exception e) {
			fail("Error while testing move in Monster class: " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveInClassDasherCase1() {
		try {
			Class<?> dasher_class = Class.forName(dasherPath);
			Method method = dasher_class.getDeclaredMethod("move", int.class);
			Object dasher_object = createDasher();


			Random rand = new Random();
			int basePosition = rand.nextInt(60) + 1;
			int distance = rand.nextInt(6) + 1;

			Field position = dasher_class.getSuperclass().getDeclaredField(
					"position");
			position.setAccessible(true);
			position.set(dasher_object, basePosition);

			Field momentumTurns = dasher_class
					.getDeclaredField("momentumTurns");
			momentumTurns.setAccessible(true);
			momentumTurns.set(dasher_object, 0);


			int newDistance = distance * 2;
			int expected_position = (basePosition + newDistance) % 100;


			method.invoke(dasher_object, distance);

			int monster_position = (int) position.get(dasher_object);

			assertEquals(
					"Dasher with momentumTurns=0: should move at 2x speed. "
							+ "After moving dasher from position "
							+ basePosition + " by distance " + distance
							+ " the new position should be", expected_position,
					monster_position);
		} catch (Exception e) {
			fail("Error while testing move in Dasher class: " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveInClassDasherCase1MomentumTurnsCheck() {
		try {
			Class<?> dasher_class = Class.forName(dasherPath);
			Method method = dasher_class.getDeclaredMethod("move", int.class);
			Object dasher_object = createDasher();


			Random rand = new Random();
			int basePosition = rand.nextInt(60) + 1;
			int distance = rand.nextInt(6) + 1;

			Field position = dasher_class.getSuperclass().getDeclaredField(
					"position");
			position.setAccessible(true);
			position.set(dasher_object, basePosition);

			Field momentumTurns = dasher_class
					.getDeclaredField("momentumTurns");
			momentumTurns.setAccessible(true);
			momentumTurns.set(dasher_object, 0);


			method.invoke(dasher_object, distance);

			int momentumTurnsAfter = (int) momentumTurns.get(dasher_object);

			assertEquals("After moving dasher from position " + basePosition
					+ " by distance " + distance
					+ " the momentumTurns shouldn't change", 0,
					momentumTurnsAfter);
		} catch (Exception e) {
			fail("Error while testing move in Dasher class: " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveInClassDasherCase2ChceckingPosition() {
		try {
			Class<?> dasher_class = Class.forName(dasherPath);
			Method method = dasher_class.getDeclaredMethod("move", int.class);
			Object dasher_object = createDasher();


			Random rand = new Random();
			int basePosition = rand.nextInt(60) + 1;
			int distance = rand.nextInt(6) + 1;
			int momentumTurns = rand.nextInt(3) + 1;

			Field position = dasher_class.getSuperclass().getDeclaredField(
					"position");
			position.setAccessible(true);
			position.set(dasher_object, basePosition);

			Field momentumTurnsField = dasher_class
					.getDeclaredField("momentumTurns");
			momentumTurnsField.setAccessible(true);
			momentumTurnsField.set(dasher_object, momentumTurns);


			int newDistance = distance * 3;
			int expected_position = (basePosition + newDistance) % 100;


			method.invoke(dasher_object, distance);

			int monster_position = (int) position.get(dasher_object);

			assertEquals("Dasher with momentumTurns= " + momentumTurns
					+ ": should move at 3x speed. "
					+ "After moving dasher from position " + basePosition
					+ " by distance " + distance
					+ " the new position should be", expected_position,
					monster_position);
		} catch (Exception e) {
			fail("Error while testing move in Dasher class: " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveInClassDasherCase2ChceckingDecrementationOfMomentum() {
		try {
			Class<?> dasher_class = Class.forName(dasherPath);
			Method method = dasher_class.getDeclaredMethod("move", int.class);
			Object dasher_object = createDasher();


			Random rand = new Random();
			int basePosition = rand.nextInt(60) + 1;
			int distance = rand.nextInt(6) + 1;
			int momentumTurns = rand.nextInt(3) + 1;

			Field position = dasher_class.getSuperclass().getDeclaredField(
					"position");
			position.setAccessible(true);
			position.set(dasher_object, basePosition);

			Field momentumTurnsField = dasher_class
					.getDeclaredField("momentumTurns");
			momentumTurnsField.setAccessible(true);
			momentumTurnsField.set(dasher_object, momentumTurns);


			method.invoke(dasher_object, distance);

			int momentumTurns_after = (int) momentumTurnsField
					.get(dasher_object);

			assertEquals("After moving dasher from position " + basePosition
					+ " by distance " + distance
					+ " the momentumTurns should be", (momentumTurns - 1),
					momentumTurns_after);
		} catch (Exception e) {
			fail("Error while testing move in Dasher class: " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveInClassMultiTaskerCase1CheckPosition() {
		try {
			Class<?> multiTasker_class = Class.forName(multiTaskerPath);
			Method method = multiTasker_class.getDeclaredMethod("move",
					int.class);
			Object multiTasker_object = createMultiTasker();


			Random rand = new Random();
			int basePosition = rand.nextInt(90) + 1;
			int distance = rand.nextInt(6) + 1;

			Field position = multiTasker_class.getSuperclass()
					.getDeclaredField("position");
			position.setAccessible(true);
			position.set(multiTasker_object, basePosition);


			method.invoke(multiTasker_object, distance);

			int expected_position = (basePosition + (distance / 2)) % 100;


			int monster_position = (int) position.get(multiTasker_object);

			assertEquals(
					"MultiTasker with normalSpeedTurns=0: should move at 1/2 speed, therefore distance is halved. "
							+ "After moving multiTasker from position "
							+ basePosition
							+ " by distance "
							+ distance
							+ " the new position should be", expected_position,
					monster_position);
		} catch (Exception e) {
			fail("Error while testing move in MultiTasker class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveInClassMultiTaskerCase1CheckNormalSpeedTurn() {
		try {
			Class<?> multiTasker_class = Class.forName(multiTaskerPath);
			Method method = multiTasker_class.getDeclaredMethod("move",
					int.class);
			Object multiTasker_object = createMultiTasker();


			Random rand = new Random();
			int basePosition = rand.nextInt(90) + 1;
			int distance = rand.nextInt(6) + 1;

			Field position = multiTasker_class.getSuperclass()
					.getDeclaredField("position");
			position.setAccessible(true);
			position.set(multiTasker_object, basePosition);


			method.invoke(multiTasker_object, distance);

			Field normalSpeedTurnField = multiTasker_class
					.getDeclaredField("normalSpeedTurns");
			normalSpeedTurnField.setAccessible(true);
			int normalSpeedTurn = (int) normalSpeedTurnField
					.get(multiTasker_object);
			assertEquals("After moving multiTasker from position "
					+ basePosition + " by distance " + distance
					+ " the normalSpeedTurn should be", 0, normalSpeedTurn);
		} catch (Exception e) {
			fail("Error while testing move in MultiTasker class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveInClassMultiTaskerCase2CheckPosition() {
		try {
			Class<?> multiTasker_class = Class.forName(multiTaskerPath);
			Method method = multiTasker_class.getDeclaredMethod("move",
					int.class);
			Object multiTasker_object = createMultiTasker();


			Random rand = new Random();
			int basePosition = rand.nextInt(90) + 1;
			int distance = rand.nextInt(6) + 1;
			int normalSpeedTurns = rand.nextInt(2) + 1;

			Field position = multiTasker_class.getSuperclass()
					.getDeclaredField("position");
			position.setAccessible(true);
			position.set(multiTasker_object, basePosition);

			Field normalSpeedTurnsField = multiTasker_class
					.getDeclaredField("normalSpeedTurns");
			normalSpeedTurnsField.setAccessible(true);
			normalSpeedTurnsField.set(multiTasker_object, normalSpeedTurns);


			method.invoke(multiTasker_object, distance);

			int expected_position = (basePosition + (distance)) % 100;


			int monster_position = (int) position.get(multiTasker_object);

			assertEquals("MultiTasker with normalSpeedTurns= "
					+ normalSpeedTurns + ": should move at normal speed "
					+ "After moving multiTasker from position " + basePosition
					+ " by distance " + distance
					+ " the new position should be", expected_position,
					monster_position);
		} catch (Exception e) {
			fail("Error while testing move in MultiTasker class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testMoveInClassMultiTaskerCase2CheckNormalSpeedTurn() {
		try {
			Class<?> multiTasker_class = Class.forName(multiTaskerPath);
			Method method = multiTasker_class.getDeclaredMethod("move",
					int.class);
			Object multiTasker_object = createMultiTasker();


			Random rand = new Random();
			int basePosition = rand.nextInt(90) + 1;
			int distance = rand.nextInt(6) + 1;
			int normalSpeedTurns = rand.nextInt(2) + 1;

			Field position = multiTasker_class.getSuperclass()
					.getDeclaredField("position");
			position.setAccessible(true);
			position.set(multiTasker_object, basePosition);

			Field normalSpeedTurnsField = multiTasker_class
					.getDeclaredField("normalSpeedTurns");
			normalSpeedTurnsField.setAccessible(true);
			normalSpeedTurnsField.set(multiTasker_object, normalSpeedTurns);


			method.invoke(multiTasker_object, distance);

			Field normalSpeedTurnField = multiTasker_class
					.getDeclaredField("normalSpeedTurns");
			normalSpeedTurnField.setAccessible(true);
			int normalSpeedTurnAfterMoving = (int) normalSpeedTurnField
					.get(multiTasker_object);
			assertEquals("After moving multiTasker from position "
					+ basePosition + " by distance " + distance
					+ " the normalSpeedTurn should be", (normalSpeedTurns - 1),
					normalSpeedTurnAfterMoving);
		} catch (Exception e) {
			fail("Error while testing move in MultiTasker class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testPerformActionInClassSwapperCardCase1PlayerCheck() {
		try {
			Class<?> swapperCard_class = Class.forName(swapperCardPath);
			Method method = swapperCard_class.getDeclaredMethod(
					"performAction", Class.forName(monsterPath),
					Class.forName(monsterPath));
			Object swapper_card = createSwapperCard();


			Object current_player = createDasher();
			Object opponent_player = createDynamo();

			Field position = Class.forName(monsterPath).getDeclaredField(
					"position");
			position.setAccessible(true);

			Random rand = new Random();
			int current_player_position, opponent_position;
			do {
				current_player_position = rand.nextInt(99);
				opponent_position = rand.nextInt(99);
			} while (current_player_position >= opponent_position);

			position.set(current_player, current_player_position);
			position.set(opponent_player, opponent_position);

			method.invoke(swapper_card, current_player, opponent_player);
			int current_player_position_after_calling = (int) position
					.get(current_player);


			assertEquals("Player was at " + current_player_position
					+ " (behind opponent at " + opponent_position
					+ "), after swap player should be at " + opponent_position,
					opponent_position, current_player_position_after_calling);
		} catch (Exception e) {
			fail("Error while testing performAction in SwapperCard class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testPerformActionInClassSwapperCardCase2PlayerCheck() {
		try {
			Class<?> swapperCard_class = Class.forName(swapperCardPath);
			Method method = swapperCard_class.getDeclaredMethod(
					"performAction", Class.forName(monsterPath),
					Class.forName(monsterPath));
			Object swapper_card = createSwapperCard();


			Object current_player = createDasher();
			Object opponent_player = createDynamo();

			Field position = Class.forName(monsterPath).getDeclaredField(
					"position");
			position.setAccessible(true);

			Random rand = new Random();
			int current_player_position, opponent_position;
			do {
				current_player_position = rand.nextInt(99);
				opponent_position = rand.nextInt(99);
			} while (current_player_position < opponent_position);

			position.set(current_player, current_player_position);
			position.set(opponent_player, opponent_position);

			method.invoke(swapper_card, current_player, opponent_player);
			int current_player_position_after_calling = (int) position
					.get(current_player);


			assertEquals("Player was at " + current_player_position
					+ " (ahead of opponent at " + opponent_position
					+ "), no swap should happen, player position should stay "
					+ current_player_position, current_player_position,
					current_player_position_after_calling);
		} catch (Exception e) {
			fail("Error while testing performAction in SwapperCard class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testPerformActionInClassStartOverCardCase1OpponentCheck() {
		try {
			Class<?> startOverCard_class = Class.forName(startOverCardPath);
			Method method = startOverCard_class.getDeclaredMethod(
					"performAction", Class.forName(monsterPath),
					Class.forName(monsterPath));
			Object startOver_card = createStartOverCard();


			Object current_player = createDasher();
			Object opponent_player = createDynamo();

			Field position = Class.forName(monsterPath).getDeclaredField(
					"position");
			position.setAccessible(true);

			Random rand = new Random();
			int current_player_position, opponent_position;
			do {
				current_player_position = rand.nextInt(98) + 1;
				opponent_position = rand.nextInt(98) + 1;
			} while (current_player_position == opponent_position);

			position.set(current_player, current_player_position);
			position.set(opponent_player, opponent_position);

			Field lucky_Field = Class.forName(startOverCardPath)
					.getSuperclass().getDeclaredField("lucky");
			lucky_Field.setAccessible(true);
			boolean lucky = (boolean) lucky_Field.get(startOver_card);
			if (!lucky) {
				lucky_Field.set(startOver_card, true);
			}

			method.invoke(startOver_card, current_player, opponent_player);


			int opponent_position_after_calling = (int) position
					.get(opponent_player);
			assertEquals("StartOverCard is lucky, opponent was at "
					+ opponent_position + " and should be sent to 0", 0,
					opponent_position_after_calling);
		} catch (Exception e) {
			fail("Error while testing performAction in StartOverCard class: "
					+ e.getMessage());
		}

	}

	@Test(timeout = 1000)
	public void testPerformActionInClassStartOverCardCase2OpponentCheck() {
		try {
			Class<?> startOverCard_class = Class.forName(startOverCardPath);
			Method method = startOverCard_class.getDeclaredMethod(
					"performAction", Class.forName(monsterPath),
					Class.forName(monsterPath));
			Object startOver_card = createStartOverCard();


			Object current_player = createDasher();
			Object opponent_player = createDynamo();

			Field position = Class.forName(monsterPath).getDeclaredField(
					"position");
			position.setAccessible(true);

			Random rand = new Random();
			int current_player_position, opponent_position;
			do {
				current_player_position = rand.nextInt(98) + 1;
				opponent_position = rand.nextInt(98) + 1;
			} while (current_player_position == opponent_position);

			position.set(current_player, current_player_position);
			position.set(opponent_player, opponent_position);

			Field lucky_Field = Class.forName(startOverCardPath)
					.getSuperclass().getDeclaredField("lucky");
			lucky_Field.setAccessible(true);
			boolean lucky = (boolean) lucky_Field.get(startOver_card);
			if (lucky) {
				lucky_Field.set(startOver_card, false);
			}

			method.invoke(startOver_card, current_player, opponent_player);


			int opponent_position_after_calling = (int) position
					.get(opponent_player);
			assertEquals("StartOverCard is not lucky, opponent was at "
					+ opponent_position + " and should NOT move",
					opponent_position, opponent_position_after_calling);
		} catch (Exception e) {
			fail("Error while testing performAction in StartOverCard class: "
					+ e.getMessage());
		}

	}


	@Test(timeout = 1000)
	public void testPerformActionInClassStartOverCardCase2CurrentPlayerCheck() {
		try {
			Class<?> startOverCard_class = Class.forName(startOverCardPath);
			Method method = startOverCard_class.getDeclaredMethod(
					"performAction", Class.forName(monsterPath),
					Class.forName(monsterPath));
			Object startOver_card = createStartOverCard();


			Object current_player = createDasher();
			Object opponent_player = createDynamo();

			Field position = Class.forName(monsterPath).getDeclaredField(
					"position");
			position.setAccessible(true);

			Random rand = new Random();
			int current_player_position, opponent_position;
			do {
				current_player_position = rand.nextInt(98) + 1;
				opponent_position = rand.nextInt(98) + 1;
			} while (current_player_position == opponent_position);

			position.set(current_player, current_player_position);
			position.set(opponent_player, opponent_position);

			Field lucky_Field = Class.forName(startOverCardPath)
					.getSuperclass().getDeclaredField("lucky");
			lucky_Field.setAccessible(true);
			boolean lucky = (boolean) lucky_Field.get(startOver_card);
			if (lucky) {
				lucky_Field.set(startOver_card, false);
			}

			method.invoke(startOver_card, current_player, opponent_player);


			int current_position_after_calling = (int) position
					.get(current_player);
			assertEquals("StartOverCard is not lucky, player was at "
					+ current_player_position + " and should be sent to 0", 0,
					current_position_after_calling);
		} catch (Exception e) {
			fail("Error while testing performAction in StartOverCard class: "
					+ e.getMessage());
		}

	}

	@Test(timeout = 1000)
	public void testOnLandInClassTransportCellCheckCallingOnLandInSuper() {
		try {
			Class<?> transportCell_class = Class.forName(transportCellPath);
			Method method = transportCell_class.getDeclaredMethod("onLand",
					Class.forName(monsterPath), Class.forName(monsterPath));
			Object conveyorBelt_object = createConveyorBelt();


			Object landing_monster = createDasher();
			Object opponent_monster = createDynamo();


			method.invoke(conveyorBelt_object, landing_monster,
					opponent_monster);


			Field monsterField = Class.forName(cellPath).getDeclaredField(
					"monster");
			monsterField.setAccessible(true);
			Object monsterInCell = monsterField.get(conveyorBelt_object);


			assertSame(
					"After onLand is called, the cell should hold a landing monster",
					landing_monster, monsterInCell);
		} catch (Exception e) {
			fail("Error while testing onLand in TransportCell class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testOnLandInClassTransportCellCheckChangingPosition() {
		try {
			Class<?> transportCell_class = Class.forName(transportCellPath);
			Method method = transportCell_class.getDeclaredMethod("onLand",
					Class.forName(monsterPath), Class.forName(monsterPath));
			Object conveyorBelt_object = createConveyorBelt();


			Object landing_monster = createDasher();
			Object opponent_monster = createDynamo();


			Random rand = new Random();
			int effect = rand.nextInt(20) + 1;
			int basePosition = rand.nextInt(99 - effect);

			Field effectField = Class.forName(transportCellPath)
					.getDeclaredField("effect");
			effectField.setAccessible(true);
			effectField.set(conveyorBelt_object, effect);

			Field positionField = Class.forName(monsterPath).getDeclaredField(
					"position");
			positionField.setAccessible(true);
			positionField.set(landing_monster, basePosition);


			method.invoke(conveyorBelt_object, landing_monster,
					opponent_monster);


			int expected_position = basePosition + effect;


			int monster_position_after_calling = positionField
					.getInt(landing_monster);


			assertEquals(
					"After onLand is called, the landing monster position should be ",
					expected_position, monster_position_after_calling);
		} catch (Exception e) {
			fail("Error while testing onLand in TransportCell class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testConveyorBeltDoesNotOverrideOnLand() {
		try {
			Class<?> converyorBelt_class = Class.forName(conveyorBeltPath);
			try {
				converyorBelt_class.getDeclaredMethod("onLand",
						Class.forName(monsterPath), Class.forName(monsterPath));
				fail("ConveyorBelt should NOT override onLand(Monster landingMonster, Monster opponentMonster) method");
			} catch (NoSuchMethodException e) {
			}
		} catch (Exception e) {
			fail("Error while accessing class ConveyorBelt: " + e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testTransportInClassTransportCellLogic() {
		try {
			Class<?> transportCell_class = Class.forName(transportCellPath);
			Method method = transportCell_class.getDeclaredMethod("transport",
					Class.forName(monsterPath));
			Object conveyorBelt_object = createConveyorBelt();


			Object landing_monster = createDasher();


			Random rand = new Random();
			int effect = rand.nextInt(20) + 1;
			int basePosition = rand.nextInt(99 - effect);

			Field effectField = Class.forName(transportCellPath)
					.getDeclaredField("effect");
			effectField.setAccessible(true);
			effectField.set(conveyorBelt_object, effect);

			Field positionField = Class.forName(monsterPath).getDeclaredField(
					"position");
			positionField.setAccessible(true);
			positionField.set(landing_monster, basePosition);


			method.invoke(conveyorBelt_object, landing_monster);


			int expected_position = basePosition + effect;


			int monster_position_after_calling = positionField
					.getInt(landing_monster);


			assertEquals("After transport is called, monster was at "
					+ basePosition + " with effect " + effect
					+ " so position should be ", expected_position,
					monster_position_after_calling);
		} catch (Exception e) {
			fail("Error while testing transport in TransportCell class: "
					+ e.getMessage());
		}
	}

	@Test(timeout = 1000)
	public void testConveyorBeltDoesNotOverrideTransport() {
		try {
			Class<?> converyorBelt_class = Class.forName(conveyorBeltPath);
			try {
				converyorBelt_class.getDeclaredMethod("transport",
						Class.forName(monsterPath));
				fail("ConveyorBelt should NOT override transport(Monster monster) method");
			} catch (NoSuchMethodException e) {
			}
		} catch (Exception e) {
			fail("Error while accessing TransportCell class: " + e.getMessage());
		}
	}


	@Test(timeout = 1000)
	public void testDynamoPowerUp() {
			try {
				Object dynamo= createDynamo();
				Object opponent = createDasher();
				Field fieldFrozen= Class.forName(monsterPath).getDeclaredField("frozen");
				fieldFrozen.setAccessible(true);
				fieldFrozen.set(opponent, false);

				Method methodExecutePowerupEffect = Class.forName(dynamoPath).getDeclaredMethod("executePowerupEffect", Class.forName(monsterPath));
				methodExecutePowerupEffect.setAccessible(true);
				methodExecutePowerupEffect.invoke(dynamo,opponent);
				assertEquals("The Dynamo's opponent frozen attribute is not updated correctly after calling the executePowerupEffect, ", true, fieldFrozen.get(opponent));


			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				fail(e.getClass()+ "Error while creating Dynamo object, make sure you use are following milestone 1 requirement.");
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				fail("Attribute frozen is expected to be in Monster class");
			}
	}

	@Test(timeout = 1000)
	public void testMultiTaskerPowerUp() {
			try {
				Object multiTasker= createMultiTasker();
				Object opponent = createSchemer();
				Field fieldNormalSpeedTurns= Class.forName(multiTaskerPath).getDeclaredField("normalSpeedTurns");
				fieldNormalSpeedTurns.setAccessible(true);
				fieldNormalSpeedTurns.set(multiTasker, (int)Math.random());

				Method methodExecutePowerupEffect = Class.forName(multiTaskerPath).getDeclaredMethod("executePowerupEffect", Class.forName(monsterPath));
				methodExecutePowerupEffect.setAccessible(true);
				methodExecutePowerupEffect.invoke(multiTasker,opponent);
				assertEquals("The MultiTasker's normalSpeedTurns attribute is not updated correctly after calling the executePowerupEffect, ", 2, fieldNormalSpeedTurns.get(multiTasker));


			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				fail(e.getClass()+ "Error while creating MultiTasker object, make sure you use are following milestone 1 requirement.");
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				fail("Attribute normalSpeedTurns is expected to be in MultiTasker class");
			}
	}

	@Test(timeout = 1000)
	public void testSchemerPowerUp() {
			try {
				Object schemer= createSchemer();
				Object opponent = createMultiTasker();
				Object stationed1 = createDynamo();
				Object stationed2 = createDasher();
				Object stationed3 = createDynamo();
				Object stationed4 = createDasher();
				Object stationed5 = createSchemer();
				Object stationed6 = createMultiTasker();

				ArrayList<Object> m = new ArrayList<Object>();
				m.add((Object)stationed1);
				m.add((Object)stationed2);
				m.add((Object)stationed3);
				m.add((Object)stationed4);
				m.add((Object)stationed5);
				m.add((Object)stationed6);

				Object board = createBoard();
				Field fieldStationedMonster= Class.forName(boardPath).getDeclaredField("stationedMonsters");
				fieldStationedMonster.setAccessible(true);
				fieldStationedMonster.set(board, m);

				Field fieldEnergy= Class.forName(monsterPath).getDeclaredField("energy");
				fieldEnergy.setAccessible(true);
				fieldEnergy.set(schemer, 20);
				fieldEnergy.set(opponent, 1000);
				fieldEnergy.set(stationed1, 100);
				fieldEnergy.set(stationed2, 250);
				fieldEnergy.set(stationed3, 5);
				fieldEnergy.set(stationed4, 50);
				fieldEnergy.set(stationed5, 0);
				fieldEnergy.set(stationed6, 8);

				Method methodExecutePowerupEffect = Class.forName(schemerPath).getDeclaredMethod("executePowerupEffect", Class.forName(monsterPath));
				methodExecutePowerupEffect.setAccessible(true);
				methodExecutePowerupEffect.invoke(schemer,opponent);
				assertEquals("The Schemer's energy attribute is not updated correctly after calling the executePowerupEffect, ", 83, fieldEnergy.get(schemer));
				assertEquals("The Opponent's energy attribute is not updated correctly after calling the executePowerupEffect,", 1190, fieldEnergy.get(opponent));
				assertEquals("The Dynamo Stationed's energy attribute is not updated correctly after calling the executePowerupEffect,", 80, fieldEnergy.get(stationed1));
				assertEquals("The Dasher Stationed's energy attribute is not updated correctly after calling the executePowerupEffect,", 240, fieldEnergy.get(stationed2));
				assertEquals("The Second Dynamo stationed's energy attribute is not updated correctly after calling the executePowerupEffect,", 0, fieldEnergy.get(stationed3));
				assertEquals("The Second Dasher stationed's energy attribute is not updated correctly after calling the executePowerupEffect,", 40, fieldEnergy.get(stationed4));
				assertEquals("The Schemer Stationed's energy attribute is not updated correctly after calling the executePowerupEffect,", 10, fieldEnergy.get(stationed5));
				assertEquals("The MultiTasker Stationed's energy attribute is not updated correctly after calling the executePowerupEffect,", 200, fieldEnergy.get(stationed6));


			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				fail(e.getClass()+ "Error while creating Schemer object, make sure you use are following milestone 1 requirement.");
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				fail("Attribute Energy is expected to be in Monster class");
			}
}

	@Test(timeout = 1000)
	public void testConfusionCardPerformAction() {
			try {
				Object confusionCard= createConfusionCard();
				Object player = createDynamo();
				Object opponent = createSchemer();

				Field fieldRole= Class.forName(monsterPath).getDeclaredField("role");
				fieldRole.setAccessible(true);
				Object original_player_role = fieldRole.get(player);
				Object original_Opponent_role = fieldRole.get(opponent);

				Field fieldconfusionTurns= Class.forName(monsterPath).getDeclaredField("confusionTurns");
				fieldconfusionTurns.setAccessible(true);
				fieldconfusionTurns.set(player, (int)Math.random());
				fieldconfusionTurns.set(opponent, (int)Math.random());

				Field fieldDuration= Class.forName(confusionCardPath).getDeclaredField("duration");
				fieldDuration.setAccessible(true);

				Method methodperformAction = Class.forName(confusionCardPath).getDeclaredMethod("performAction", Class.forName(monsterPath),Class.forName(monsterPath));
				methodperformAction.setAccessible(true);
				methodperformAction.invoke(confusionCard,player,opponent);
				assertEquals("The Player's confusionTurns attribute is not updated correctly after calling the performAction, ", fieldDuration.get(confusionCard), fieldconfusionTurns.get(player));
				assertEquals("The Opponent's confusionTurns attribute is not updated correctly after calling the performAction, ", fieldDuration.get(confusionCard), fieldconfusionTurns.get(opponent));
				assertEquals("The confusionCard should swap the players' roles, ", original_Opponent_role, fieldRole.get(player));
				assertEquals("The confusionCard should swap the players' roles, ", original_player_role, fieldRole.get(opponent));


			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				fail(e.getClass()+ "Error while creating ConfusionCard object, make sure you use are following milestone 1 requirement.");
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				fail("Attribute confusionTurns is expected to be in Monster class");
			}
	}


	@Test(timeout = 1000)
	public void testmonsterDecrementConfusionCaseLastConfusionTurn() {
			try {
				Object player = createDynamo();

				Field fieldConfusionTurns= Class.forName(monsterPath).getDeclaredField("confusionTurns");
				fieldConfusionTurns.setAccessible(true);
				fieldConfusionTurns.set(player, 1);

				Field fieldRole= Class.forName(monsterPath).getDeclaredField("role");
				fieldRole.setAccessible(true);
				Field fieldOriginalRole= Class.forName(monsterPath).getDeclaredField("originalRole");
				fieldOriginalRole.setAccessible(true);

				Method methodDecrementConfusion = Class.forName(monsterPath).getDeclaredMethod("decrementConfusion");
				methodDecrementConfusion.setAccessible(true);
				methodDecrementConfusion.invoke(player);
				assertEquals("The Player should no longer be confused, ", 0, fieldConfusionTurns.get(player));
				assertEquals("The Player should no longer be confused, ", fieldOriginalRole.get(player), fieldRole.get(player));


			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				fail(e.getClass()+ "Error while creating Monster object, make sure you use are following milestone 1 requirement.");
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				fail("Attribute confusionTurns is expected to be in Monster class");
			}
	}

	@Test(timeout = 1000)
	public void testmonsterDecrementConfusionCaseRemainConfused() {
			try {
				Object player = createDynamo();

				Field fieldConfusionTurns= Class.forName(monsterPath).getDeclaredField("confusionTurns");
				fieldConfusionTurns.setAccessible(true);
				fieldConfusionTurns.set(player,2);

				Field fieldRole= Class.forName(monsterPath).getDeclaredField("role");
				fieldRole.setAccessible(true);
				Field fieldOriginalRole= Class.forName(monsterPath).getDeclaredField("originalRole");
				fieldOriginalRole.setAccessible(true);

				Object role_Laugher = Enum.valueOf((Class<Enum>) Class.forName(rolePath), "LAUGHER");
				Object role_Scarer = Enum.valueOf((Class<Enum>) Class.forName(rolePath), "SCARER");

				fieldRole.set(player, role_Laugher);
				fieldOriginalRole.set(player, role_Scarer);

				Method methodDecrementConfusion = Class.forName(monsterPath).getDeclaredMethod("decrementConfusion");
				methodDecrementConfusion.setAccessible(true);
				methodDecrementConfusion.invoke(player);
				assertEquals("The Player's confusionTurns attributes is not updated correctly after calling decrementConfusion, ", 1, fieldConfusionTurns.get(player));
				assertNotEquals("The Player should still be confused, ", fieldOriginalRole.get(player), fieldRole.get(player));


			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				fail(e.getClass()+ "Error while creating Monster object, make sure you use are following milestone 1 requirement.");
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				fail("Attributes role, originalRole, and confusionTurns are expected to be in Monster class");
			}
	}

	@Test(timeout = 1000)
	public void testMonsterIsConfusedCaseFalse() {
			try {
				Object monster = createDynamo();
				Field fieldConfusionTurns= Class.forName(monsterPath).getDeclaredField("confusionTurns");
				fieldConfusionTurns.setAccessible(true);
				fieldConfusionTurns.set(monster, 0);

				Method methodIsConfused = Class.forName(monsterPath).getDeclaredMethod("isConfused");
				methodIsConfused.setAccessible(true);
				Object confused_return = methodIsConfused.invoke(monster);
				assertFalse("The monster should NOT be confused", (boolean)confused_return);


			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				fail(e.getClass()+ "Error while creating Dynamo object, make sure you use are following milestone 1 requirement.");
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				fail("Attribute confusionTurns is expected to be in Monster class");
			}
	}

	@Test(timeout = 1000)
	public void testGameUsePowerupCaseNotEnoughEnergy() {
			try {
				Object monster = createDynamo();
				Field fieldEnergy= Class.forName(monsterPath).getDeclaredField("energy");
				fieldEnergy.setAccessible(true);
				fieldEnergy.set(monster, (int)Math.random()*(500));
				Object game = null;
				try {
					game = createGameForTesting();
				} catch (Exception e) {
					e.printStackTrace();
					fail("Error while creating Game object, make sure you use are following milestone 1 requirement.");
				}
				Field fieldCurrent= Class.forName(gamePath).getDeclaredField("current");
				fieldCurrent.setAccessible(true);
				fieldCurrent.set(game, monster);

				Method methodUsePowerup = Class.forName(gamePath).getDeclaredMethod("usePowerup");
				methodUsePowerup.setAccessible(true);
				methodUsePowerup.invoke(game);

			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException  e) {
				e.printStackTrace();
				fail(e.getClass()+ "Error while creating Dynamo object, make sure you use are following milestone 1 requirement.");
			} catch (NoSuchFieldException e) {
				fail("Attribute energy is expected to be in Monster class");
			}
				catch (InvocationTargetException e) {
				if(!(e.getCause().toString().contains("OutOfEnergyException"))) {
					fail("You can't use powerup if you don't have enough energy ");
				}
			}
	}

	@Test(timeout = 1000)
	public void testGameUsePowerupCaseHasEnoughEnergy() {
			try {
				Object monster = createDynamo();
				Field fieldEnergy= Class.forName(monsterPath).getDeclaredField("energy");
				fieldEnergy.setAccessible(true);
				fieldEnergy.set(monster, (int)Math.random()+500);
				Object intialEnergy = fieldEnergy.get(monster);

				Object opponent = createDasher();
				Field fieldFreeze= Class.forName(monsterPath).getDeclaredField("frozen");
				fieldFreeze.setAccessible(true);
				fieldFreeze.set(opponent, false);

				Object game = null;
				try {
					game = createGameForTesting();
				} catch (Exception e) {
					e.printStackTrace();
					fail("Error while creating Game object, make sure you use are following milestone 1 requirement.");
				}
				Field fieldCurrent= Class.forName(gamePath).getDeclaredField("current");
				fieldCurrent.setAccessible(true);
				fieldCurrent.set(game, monster);
				Field fieldPlayer= Class.forName(gamePath).getDeclaredField("player");
				fieldPlayer.setAccessible(true);
				fieldPlayer.set(game, monster);
				Field fieldOpponent= Class.forName(gamePath).getDeclaredField("opponent");
				fieldOpponent.setAccessible(true);
				fieldOpponent.set(game, opponent);

				Method methodUsePowerup = Class.forName(gamePath).getDeclaredMethod("usePowerup");
				methodUsePowerup.setAccessible(true);
				methodUsePowerup.invoke(game);
				assertEquals("Powerup cost should be deducted after calling usePowerup",(int)intialEnergy-500, fieldEnergy.get(monster));
				assertEquals("Powerup effect should be executed after calling usePowerup",true, fieldFreeze.get(opponent));

			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				fail(e.getClass()+ "Error while creating Dynamo object, make sure you use are following milestone 1 requirement.");
			} catch (NoSuchFieldException e) {
				fail("Attribute energy is expected to be in Monster class");

			}
	}

	@Test(timeout = 1000)
	public void testMonsterCellOnLandCaseSameRole() {
			try {
				Object monsterCell = createMonsterCell();
				Field fieldCellMonster= Class.forName(monsterCellPath).getDeclaredField("cellMonster");
				fieldCellMonster.setAccessible(true);

				Object CellMonster = createSchemer();
				fieldCellMonster.set(monsterCell, CellMonster);

				Object role = randomRole();

				Field fieldMonsterRole= Class.forName(monsterPath).getDeclaredField("role");
				fieldMonsterRole.setAccessible(true);
				fieldMonsterRole.set(CellMonster, role);

				Object landingMonster = createDynamo();
				fieldMonsterRole.set(landingMonster, role);

				Object opponentMonster = createDasher();
				Field fieldFreeze= Class.forName(monsterPath).getDeclaredField("frozen");
				fieldFreeze.setAccessible(true);
				fieldFreeze.set(opponentMonster, false);


				Method methodOnLand = Class.forName(monsterCellPath).getDeclaredMethod("onLand",Class.forName(monsterPath),Class.forName(monsterPath));
				methodOnLand.setAccessible(true);
				methodOnLand.invoke(monsterCell,landingMonster,opponentMonster);
				assertEquals("Powerup effect should be executed when encountering an ally",true, fieldFreeze.get(opponentMonster));

			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				fail(e.getClass()+ "Error while creating Dynamo object, make sure you use are following milestone 1 requirement.");
			} catch (NoSuchFieldException e) {
				fail("Attribute energy is expected to be in Monster class");

			}
	}

	@Test(timeout = 1000)
	public void testMonsterCellOnLandCaseDifferentRolesCellhasMore() {
			try {
				Object monsterCell = createMonsterCell();
				Field fieldCellMonster= Class.forName(monsterCellPath).getDeclaredField("cellMonster");
				fieldCellMonster.setAccessible(true);

				Object CellMonster = createSchemer();
				fieldCellMonster.set(monsterCell, CellMonster);

				Object role_Laugher = Enum.valueOf((Class<Enum>) Class.forName(rolePath), "LAUGHER");
				Object role_Scarer = Enum.valueOf((Class<Enum>) Class.forName(rolePath), "SCARER");

				Field fieldMonsterRole= Class.forName(monsterPath).getDeclaredField("role");
				fieldMonsterRole.setAccessible(true);
				fieldMonsterRole.set(CellMonster, role_Laugher);

				Object landingMonster = createDynamo();
				fieldMonsterRole.set(landingMonster, role_Scarer);

				Object opponentMonster = createDasher();

				Field fieldEnergy= Class.forName(monsterPath).getDeclaredField("energy");
				fieldEnergy.setAccessible(true);
				fieldEnergy.set(CellMonster, 1000);
				fieldEnergy.set(landingMonster, 500);

				Method methodOnLand = Class.forName(monsterCellPath).getDeclaredMethod("onLand",Class.forName(monsterPath),Class.forName(monsterPath));
				methodOnLand.setAccessible(true);
				methodOnLand.invoke(monsterCell,landingMonster,opponentMonster);
				assertEquals("Swapping of energy only happens if landing monster has more energy than the cell monster",1000, fieldEnergy.get(CellMonster));
				assertEquals("Swapping of energy only happens if landing monster has more energy than the cell monster",500, fieldEnergy.get(landingMonster));

			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				fail(e.getClass()+ "Error while creating Dynamo object, make sure you use are following milestone 1 requirement.");
			} catch (NoSuchFieldException e) {
				fail("Attribute energy is expected to be in Monster class");

			}
	}


	@Test(timeout = 1000)
	public void testMonsterCellOnLandCaseDifferentRolesLandinghasMoreShielded() {
			try {
				Object monsterCell = createMonsterCell();
				Field fieldCellMonster= Class.forName(monsterCellPath).getDeclaredField("cellMonster");
				fieldCellMonster.setAccessible(true);

				Object CellMonster = createSchemer();
				fieldCellMonster.set(monsterCell, CellMonster);

				Object role_Laugher = Enum.valueOf((Class<Enum>) Class.forName(rolePath), "LAUGHER");
				Object role_Scarer = Enum.valueOf((Class<Enum>) Class.forName(rolePath), "SCARER");

				Field fieldMonsterRole= Class.forName(monsterPath).getDeclaredField("role");
				fieldMonsterRole.setAccessible(true);
				fieldMonsterRole.set(CellMonster, role_Laugher);

				Object landingMonster = createDynamo();
				fieldMonsterRole.set(landingMonster, role_Scarer);

				Object opponentMonster = createDasher();

				Field fieldEnergy= Class.forName(monsterPath).getDeclaredField("energy");
				fieldEnergy.setAccessible(true);
				fieldEnergy.set(CellMonster, 500);
				fieldEnergy.set(landingMonster, 1000);

				Field fieldShielded= Class.forName(monsterPath).getDeclaredField("shielded");
				fieldShielded.setAccessible(true);
				fieldShielded.set(landingMonster, true);

				Method methodOnLand = Class.forName(monsterCellPath).getDeclaredMethod("onLand",Class.forName(monsterPath),Class.forName(monsterPath));
				methodOnLand.setAccessible(true);
				methodOnLand.invoke(monsterCell,landingMonster,opponentMonster);
				assertEquals("Swapping of energy should happen if landing monster has less energy than the cell monster",1010, fieldEnergy.get(CellMonster));
				assertEquals("Landing monster is shielded",1000, fieldEnergy.get(landingMonster));

			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				fail(e.getClass()+ "Error while creating Dynamo object, make sure you use are following milestone 1 requirement.");
			} catch (NoSuchFieldException e) {
				fail("Attribute energy is expected to be in Monster class");

			}
	}
	@Test(timeout = 1000)
	public void testMonsterCellOnLandCaseDifferentRolesLandinghasMoreUnshielded() {
			try {
				Object monsterCell = createMonsterCell();
				Field fieldCellMonster= Class.forName(monsterCellPath).getDeclaredField("cellMonster");
				fieldCellMonster.setAccessible(true);

				Object CellMonster = createSchemer();
				fieldCellMonster.set(monsterCell, CellMonster);


				Object role_Laugher = Enum.valueOf((Class<Enum>) Class.forName(rolePath), "LAUGHER");
				Object role_Scarer = Enum.valueOf((Class<Enum>) Class.forName(rolePath), "SCARER");

				Field fieldMonsterRole= Class.forName(monsterPath).getDeclaredField("role");
				fieldMonsterRole.setAccessible(true);
				fieldMonsterRole.set(CellMonster, role_Laugher);

				Object landingMonster = createDynamo();
				fieldMonsterRole.set(landingMonster, role_Scarer);

				Field fieldShielded= Class.forName(monsterPath).getDeclaredField("shielded");
				fieldShielded.setAccessible(true);
				fieldShielded.set(landingMonster, false);

				Object opponentMonster = createDasher();

				Field fieldEnergy= Class.forName(monsterPath).getDeclaredField("energy");
				fieldEnergy.setAccessible(true);
				fieldEnergy.set(CellMonster, 500);
				fieldEnergy.set(landingMonster, 1000);

				Method methodOnLand = Class.forName(monsterCellPath).getDeclaredMethod("onLand",Class.forName(monsterPath),Class.forName(monsterPath));
				methodOnLand.setAccessible(true);
				methodOnLand.invoke(monsterCell,landingMonster,opponentMonster);
				assertEquals("Swap Energy if landing monster has more energy than Cell Monster",1010, fieldEnergy.get(CellMonster));
				assertEquals("Swap Energy if landing monster has more energy than Cell Monster",0, fieldEnergy.get(landingMonster));

			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				fail(e.getClass()+ "Error while creating Dynamo object, make sure you use are following milestone 1 requirement.");
			} catch (NoSuchFieldException e) {
				fail("Attribute energy is expected to be in Monster class");

			}
	}







	private boolean contains(int[] arr, int value) {
		for (int x : arr) {
			if (x == value) return true;
		}
		return false;
	}
	private void testMethodIsAbstract(Method aMethod) {
		assertTrue(aMethod.getName() + " should be an abtract Method.",
				Modifier.isAbstract(aMethod.getModifiers()));
	}


	private static void savingCardsCSV() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("cards.csv"));
		cards_csv= new ArrayList<>();
		while (br.ready()) {
			String nextLine = br.readLine();
			cards_csv.add(nextLine);
		}
	}
	private static void savingCellsCSV() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("cells.csv"));
		cells_csv= new ArrayList<>();
		while (br.ready()) {
			String nextLine = br.readLine();
			cells_csv.add(nextLine);
		}
	}
	private static void savingMonstersCSV() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("monsters.csv"));
		monsters_csv= new ArrayList<>();
		while (br.ready()) {
			String nextLine = br.readLine();
			monsters_csv.add(nextLine);
		}
	}


	private static void reWriteCardsCSVForLoadCards() throws FileNotFoundException {
		PrintWriter csvWriter = new PrintWriter("cards.csv");

		for (int i = 0; i < cards_csv.size(); i++) {
			csvWriter.println(cards_csv.get(i));
		}
		csvWriter.flush();
		csvWriter.close();

	}
	private static void reWriteCellsCSVForLoadCards() throws FileNotFoundException {
		PrintWriter csvWriter = new PrintWriter("cells.csv");

		for (int i = 0; i < cells_csv.size(); i++) {
			csvWriter.println(cells_csv.get(i));
		}
		csvWriter.flush();
		csvWriter.close();

	}
	private static void reWriteMonstersCSVForLoadCards() throws FileNotFoundException {
		PrintWriter csvWriter = new PrintWriter("monsters.csv");

		for (int i = 0; i < monsters_csv.size(); i++) {
			csvWriter.println(monsters_csv.get(i));
		}
		csvWriter.flush();
		csvWriter.close();

	}

	private ArrayList<String> writeCardsCSVForDataLoader() throws FileNotFoundException {
		PrintWriter csvWriter = new PrintWriter("cards.csv");
		ArrayList<String> list = new ArrayList<>();
		csvWriter.println("SWAPPER,Position Swap,Swap places with opponent if behind,4");
		list.add("SWAPPER,Position Swap,Swap places with opponent if behind,4");
		csvWriter.println("SHIELD,Super Shield,Block next negative effect,5");
		list.add("SHIELD,Super Shield,Block next negative effect,5");
		csvWriter.println("ENERGYSTEAL,Small Snatcher,Steal 50 energy from opponent,3,50");
		list.add("ENERGYSTEAL,Small Snatcher,Steal 50 energy from opponent,3,50");
		csvWriter.println("ENERGYSTEAL,Sneaky Thief,Steal 100 energy from opponent,2,100");
		list.add("ENERGYSTEAL,Sneaky Thief,Steal 100 energy from opponent,2,100");
		csvWriter.println("STARTOVER,Contamination Code,Player return to first cell,2,false");
		list.add("STARTOVER,Contamination Code,Player return to first cell,2,false");
		csvWriter.println("STARTOVER,2319 Alert,Opponent returns to first cell,3,true");
		list.add("STARTOVER,2319 Alert,Opponent returns to first cell,3,true");
		csvWriter.println("CONFUSION,Mind Scramble,Both players confused for 2 turns,3,2");
		list.add("CONFUSION,Mind Scramble,Both players confused for 2 turns,3,2");
		csvWriter.println("CONFUSION,Total Confusion,Both players confused for 3 turns,2,3");
		list.add("CONFUSION,Total Confusion,Both players confused for 3 turns,2,3");
		csvWriter.flush();
		csvWriter.close();
		return list;
	}

	private ArrayList<String> writeCellsCSVForDataLoader() throws FileNotFoundException {
		PrintWriter csvWriter = new PrintWriter("cells.csv");
		ArrayList<String> list = new ArrayList<>();
		csvWriter.println("Belt1,6");
		list.add("Belt1,6");
		csvWriter.println("Belt2,22");
		list.add("Belt2,22");
		csvWriter.println("Sock1,-32");
		list.add("Sock1,-32");
		csvWriter.println("Sock2,-42");
		list.add("Sock2,-42");
		csvWriter.println("DoorScarer,SCARER,50");
		list.add("DoorScarer,SCARER,50");
		csvWriter.println("DoorLaugher,LAUGHER,50");
		list.add("DoorLaugher,LAUGHER,50");
		csvWriter.flush();
		csvWriter.close();
		return list;
	}

	private ArrayList<String> writeMonstersCSVForDataLoader() throws FileNotFoundException {
		PrintWriter csvWriter = new PrintWriter("monsters.csv");
		ArrayList<String> list = new ArrayList<>();
		csvWriter.println("DASHER,Mike Wazowski,Fast and funny the comedy speedster,LAUGHER,100");
		list.add("DASHER,Mike Wazowski,Fast and funny the comedy speedster,LAUGHER,100");
		csvWriter.println("DYNAMO,James P. Sullivan,The top scarer powerful and confident,SCARER,300");
		list.add("DYNAMO,James P. Sullivan,The top scarer powerful and confident,SCARER,300");
		csvWriter.println("SCHEMER,Randall Boggs,Sneaky and cunning always has an angle,SCARER,20");
		list.add("SCHEMER,Randall Boggs,Sneaky and cunning always has an angle,SCARER,20");
		csvWriter.println("MULTITASKER,Celia Mae,Organized receptionist handles everything,LAUGHER,50");
		list.add("MULTITASKER,Celia Mae,Organized receptionist handles everything,LAUGHER,50");
		csvWriter.println("DASHER,Fungus,Timid assistant quick but nervous,LAUGHER,50");
		list.add("DASHER,Fungus,Timid assistant quick but nervous,LAUGHER,50");
		csvWriter.println("DYNAMO,Yeti,Banished snow monster surprisingly cheerful,LAUGHER,100");
		list.add("DYNAMO,Yeti,Banished snow monster surprisingly cheerful,LAUGHER,100");
		csvWriter.flush();
		csvWriter.close();
		return list;
	}


	private Object createGameForTesting() throws Exception {
		savingCardsCSV();
		savingMonstersCSV();
		try {
			writeCardsCSVForDataLoader();
			writeMonstersCSVForDataLoader();
			Object role = Enum.valueOf((Class<Enum>) Class.forName(rolePath), "SCARER");
			Constructor<?> gameConstructor = Class.forName(gamePath).getConstructor(Class.forName(rolePath));
			return gameConstructor.newInstance(role);
		} finally {
			reWriteCardsCSVForLoadCards();
			reWriteMonstersCSVForLoadCards();
		}
	}

	private Object createSwapperCard()  throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		String name = generateRandomString(5 + new Random().nextInt(11));
		String description = generateRandomString(10 + new Random().nextInt(21));
		int rarity = 1 + new Random().nextInt(10);
		Constructor<?> c = Class.forName(swapperCardPath).getConstructor(String.class, String.class, int.class);
		return c.newInstance(name, description, Integer.valueOf(rarity));
	}

	private Object createStartOverCard()  throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		String name = generateRandomString(5 + new Random().nextInt(11));
		String description = generateRandomString(10 + new Random().nextInt(21));
		int rarity = 1 + new Random().nextInt(10);
		boolean lucky = new Random().nextBoolean();
		Constructor<?> c = Class.forName(startOverCardPath).getConstructor(String.class, String.class, int.class, boolean.class);
		return c.newInstance(name, description, Integer.valueOf(rarity), Boolean.valueOf(lucky));
	}

	private Object createShieldCard()  throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		String name = generateRandomString(5 + new Random().nextInt(11));
		String description = generateRandomString(10 + new Random().nextInt(21));
		int rarity = 1 + new Random().nextInt(10);
		Constructor<?> c = Class.forName(shieldCardPath).getConstructor(String.class, String.class, int.class);
		return c.newInstance(name, description, Integer.valueOf(rarity));
	}

	private Object createEnergyStealCard()  throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		String name = generateRandomString(5 + new Random().nextInt(11));
		String description = generateRandomString(10 + new Random().nextInt(21));
		int rarity = 1 + new Random().nextInt(10);
		int energy = 1 + new Random().nextInt(200);
		Constructor<?> c = Class.forName(energyStealCardPath).getConstructor(String.class, String.class, int.class, int.class);
		return c.newInstance(name, description, Integer.valueOf(rarity), Integer.valueOf(energy));
	}

	private Object createConfusionCard()  throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		String name = generateRandomString(5 + new Random().nextInt(11));
		String description = generateRandomString(10 + new Random().nextInt(21));
		int rarity = 1 + new Random().nextInt(10);
		int duration = 1 + new Random().nextInt(5);
		Constructor<?> c = Class.forName(confusionCardPath).getConstructor(String.class, String.class, int.class, int.class);
		return c.newInstance(name, description, Integer.valueOf(rarity), Integer.valueOf(duration));
	}

	private Object randomRole() throws ClassNotFoundException {
		String value = new Random().nextBoolean() ? "SCARER" : "LAUGHER";
		return Enum.valueOf((Class<Enum>) Class.forName(rolePath), value);
	}

	private Object createDasher()  throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		String name = generateRandomString(5 + new Random().nextInt(11));
		String description = generateRandomString(10 + new Random().nextInt(21));
		Object role = randomRole();
		int energy = new Random().nextInt(501);
		Constructor<?> c = Class.forName(dasherPath).getConstructor(String.class, String.class, Class.forName(rolePath), int.class);
		return c.newInstance(name, description, role, Integer.valueOf(energy));
	}

	private Object createDynamo() throws  ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException   {
		String name = generateRandomString(5 + new Random().nextInt(11));
		String description = generateRandomString(10 + new Random().nextInt(21));
		Object role = randomRole();
		int energy = new Random().nextInt(501);
		Constructor<?> c = Class.forName(dynamoPath).getConstructor(String.class, String.class, Class.forName(rolePath), int.class);
		return c.newInstance(name, description, role, Integer.valueOf(energy));
	}

	private Object createMultiTasker() throws   ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException   {
		String name = generateRandomString(5 + new Random().nextInt(11));
		String description = generateRandomString(10 + new Random().nextInt(21));
		Object role = randomRole();
		int energy = new Random().nextInt(501);
		Constructor<?> c = Class.forName(multiTaskerPath).getConstructor(String.class, String.class, Class.forName(rolePath), int.class);
		return c.newInstance(name, description, role, Integer.valueOf(energy));
	}

	private Object createSchemer() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException   {
		String name = generateRandomString(5 + new Random().nextInt(11));
		String description = generateRandomString(10 + new Random().nextInt(21));
		Object role = randomRole();
		int energy = new Random().nextInt(501);
		Constructor<?> c = Class.forName(schemerPath).getConstructor(String.class, String.class, Class.forName(rolePath), int.class);
		return c.newInstance(name, description, role, Integer.valueOf(energy));
	}

	private Object createCell() throws  ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException   {
		String name = generateRandomString(5 + new Random().nextInt(11));
		Constructor<?> c = Class.forName(cellPath).getConstructor(String.class);
		return c.newInstance(name);
	}

	private Object createDoorCell() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException   {
		String name = generateRandomString(5 + new Random().nextInt(11));
		Object role = randomRole();
		int energy = new Random().nextInt(201);
		Constructor<?> c = Class.forName(doorCellPath).getConstructor(String.class, Class.forName(rolePath), int.class);
		return c.newInstance(name, role, Integer.valueOf(energy));
	}

	private Object createCardCell() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException   {
		String name = generateRandomString(5 + new Random().nextInt(11));
		Constructor<?> c = Class.forName(cardCellPath).getConstructor(String.class);
		return c.newInstance(name);
	}

	private Object createMonsterCell() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException   {
		String name = generateRandomString(5 + new Random().nextInt(11));
		Object cellMonster = createDasher();
		Constructor<?> c = Class.forName(monsterCellPath).getConstructor(String.class, Class.forName(monsterPath));
		return c.newInstance(name, cellMonster);
	}

	private Object createConveyorBelt() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException   {
		String name = generateRandomString(5 + new Random().nextInt(11));
		int effect = 1 + new Random().nextInt(20);
		Constructor<?> c = Class.forName(conveyorBeltPath).getConstructor(String.class, int.class);
		return c.newInstance(name, Integer.valueOf(effect));
	}

	private Object createContaminationSock() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException   {
		String name = generateRandomString(5 + new Random().nextInt(11));
		int effect = -1 - new Random().nextInt(20);
		Constructor<?> c = Class.forName(contaminationSockPath).getConstructor(String.class, int.class);
		return c.newInstance(name, Integer.valueOf(effect));
	}


	private String generateRandomString(int length) {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(characters.charAt(random.nextInt(characters.length())));
		}
		return sb.toString();
	}

	private Object createBoard() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ArrayList<Object> readCards = new ArrayList<Object>();
		Constructor<?> c = Class.forName(boardPath).getConstructor(ArrayList.class);
		return c.newInstance(readCards);
	}

}
