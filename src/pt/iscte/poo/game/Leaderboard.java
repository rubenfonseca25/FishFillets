package pt.iscte.poo.game;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class Leaderboard {

	private static final String HEADER = "Position    |   Name   |    Moves";
	private static final String FOLDER = "leaderboard";
	private static final String FILE_NAME = "leaderboard.txt";

	//Salva o novo score na leaderboard
	static void saveScore(String name, int moves) {
		//Se o nome for vazio ou só espaços passa a ser "Player"
		if (name == null || name.trim().isEmpty())
			name = "Player";
		name = name.trim();

		//Garante que a pasta existe
		File dir = new File(FOLDER);
		if (!dir.exists())
			dir.mkdirs();
		//Ficheiro da leaderboard
		File file = new File(dir, FILE_NAME);

		//Lê os scores existentes adiciona o novo e reescreve o ficheiro
		List<Entry> entries = readExisting(file);
		entries.add(new Entry(name, moves));
		//Função lambda para ordenar por número de movimentos
		entries.sort(Comparator.comparingInt(e -> e.moves));
		rewrite(file, entries);
	}

	//Lê os scores existentes da leaderboard
	private static List<Entry> readExisting(File file) {
		//Lista para armazenar as entradas
		List<Entry> entries = new ArrayList<>();
		if (!file.exists())
			return entries;

		try (Scanner sc = new Scanner(file)) {
			//Passar a primeira linha a frente
			if (sc.hasNextLine())
				sc.nextLine();
			while (sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				//Ignora linhas vazias
				if (line.isEmpty())
					continue;
				//Divide a linha por "|" tive de usar o \\ para não dar erro
				String[] parts = line.split("\\|");
				//Se não tiver as 3 partes necessárias ignora a linha
				if (parts.length < 3)
					continue;
				String name = parts[1].trim();
				String movesStr = parts[2].trim();
				//Tenta converter os movimentos para inteiro se não conseguir ignora a linha
				try {
					int moves = Integer.parseInt(movesStr);
					entries.add(new Entry(name, moves));
				} 
				catch (NumberFormatException e) {
				}
			}
		}
		catch (IOException e) {
		}
		return entries;
	}

	//Reescreve o ficheiro da leaderboard com as entradas fornecidas
	private static void rewrite(File file, List<Entry> entries) {
		try (PrintWriter pw = new PrintWriter(file)) {
			pw.println(HEADER);
			int position = 1;
			for (Entry e : entries) {
				pw.println(position + "    |    " + e.name + "    |    " + e.moves);
				position++;
			}
		} catch (IOException e) {
		}
	}

	//Classe aninhada estática para representar uma entrada na leaderboard
	//Podem ser criadas instâncias (objetos) da classe aninhada sem que exista uma instância da classe principal
	private static class Entry {
		final String name;
		final int moves;

		//Construtor para criar uma nova entrada na leaderboard
		Entry(String name, int moves) {
			this.name = name;
			this.moves = moves;
		}
	}
}
