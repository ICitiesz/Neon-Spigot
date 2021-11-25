package com.islandstudio.neon.Stable.Old.Utilities;

import com.islandstudio.neon.Stable.New.Utilities.ServerCFGHandler;
import com.islandstudio.neon.Stable.Old.Initialization.FolderManager.FolderList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatLogger {
    private static final ArrayList<String> notification = new ArrayList<>();

    public static void initialize() throws IOException, ParseException {
        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy_MM_dd");

        if (Bukkit.getServer().getOnlineMode()) {
            if (ServerCFGHandler.getValue().get("ChatLogging").equals(true)) {
                HashMap<Integer, File> fileMap = new HashMap<>();

                File[] listFiles = FolderList.getFolder_2a_2.listFiles();

                File logFile_1 = new File(FolderList.getFolder_2a_2, dateFormatter.format(date) + "-chat_" + 1 + ".log");

                if (!logFile_1.exists()) {
                    try {
                        boolean isLogCreated = logFile_1.createNewFile();

                        if (!isLogCreated) {
                            notification.add(ChatColor.RED + "Failed to create log file" + ChatColor.GRAY + "\"" + ChatColor.WHITE + logFile_1.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (listFiles != null) {
                        for (File file : listFiles) {
                            if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("log")) {
                                String logFilesName = file.getName().substring(0, 16);

                                if (logFilesName.equalsIgnoreCase(dateFormatter.format(date) + "-chat_")) {
                                    String index = file.getName().split("_")[3].split("\\.")[0];

                                    fileMap.put(Integer.parseInt(index), file);
                                }
                            }
                        }

                        /* --------------------------------------------------------------------------------------------------- */
                        File lastFile = fileMap.get(fileMap.size());

                        if (lastFile.exists()) {
                            String index = lastFile.getName().split("_")[3].split("\\.")[0];

                            int fileIndex = Integer.parseInt(index);

                            File logFile_2 = new File(FolderList.getFolder_2b_2, dateFormatter.format(date) + "-chat_" + (fileIndex + 1) + ".log");

                            if (!logFile_2.exists()) {
                                try {
                                    boolean isNextLogCreated = logFile_2.createNewFile();

                                    if (!isNextLogCreated) {
                                        notification.add(ChatColor.RED + "Failed to log file" + ChatColor.GRAY + "\"" + ChatColor.WHITE + logFile_2.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        fileMap.clear();
                    }
                }
            }
        } else {
            if (ServerCFGHandler.getValue().get("ChatLogging").equals(true)) {
                HashMap<Integer, File> fileMap = new HashMap<>();

                File[] listFiles = FolderList.getFolder_2b_2.listFiles();

                File logFile_1 = new File(FolderList.getFolder_2b_2, dateFormatter.format(date) + "-chat_" + 1 + ".log");

                if (!logFile_1.exists()) {
                    try {
                        boolean isLogCreated = logFile_1.createNewFile();

                        if (!isLogCreated) {
                            notification.add(ChatColor.RED + "Failed to log file" + ChatColor.GRAY + "\"" + ChatColor.WHITE + logFile_1.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (listFiles != null) {
                        for (File file : listFiles) {
                            if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("log")) {
                                String logFilesName = file.getName().substring(0, 16);

                                if (logFilesName.equalsIgnoreCase(dateFormatter.format(date) + "-chat_")) {
                                    String index = file.getName().split("_")[3].split("\\.")[0];

                                    fileMap.put(Integer.parseInt(index), file);
                                }
                            }
                        }

                        /* --------------------------------------------------------------------------------------------------- */
                        File lastFile = fileMap.get(fileMap.size());

                        if (lastFile.exists()) {
                            String index = lastFile.getName().split("_")[3].split("\\.")[0];

                            int fileIndex = Integer.parseInt(index);

                            File logFile_2 = new File(FolderList.getFolder_2b_2, dateFormatter.format(date) + "-chat_" + (fileIndex + 1) + ".log");

                            if (!logFile_2.exists()) {
                                try {
                                    boolean isNextLogCreated = logFile_2.createNewFile();

                                    if (!isNextLogCreated) {
                                        notification.add(ChatColor.RED + "Failed to log file" + ChatColor.GRAY + "\"" + ChatColor.WHITE + logFile_2.getName() + ChatColor.GRAY + "\"" + ChatColor.RED + "!");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        fileMap.clear();
                    }
                }
            }
        }

        if (notification.size() > 0) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~Initialization Error~~~~~~~~~~~~~~~~~~~~~~");
            for (String notify : notification) {
                System.out.println(notify);
            }
            notification.clear();
        }
    }

    public static File getChatLog() {
        Map<Integer, File> fileMap = new TreeMap<>();
        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy_MM_dd");

        if (Bukkit.getServer().getOnlineMode()) {
            File[] listFiles = FolderList.getFolder_2a_2.listFiles();

            if (listFiles != null) {
                for (File file : listFiles) {
                    if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("log")) {
                        String logFilesName = file.getName().substring(0, 16);

                        if (logFilesName.equalsIgnoreCase(dateFormatter.format(date) + "-chat_")) {
                            String index = file.getName().split("_")[3].split("\\.")[0];

                            fileMap.put(Integer.parseInt(index), file);
                        }
                    }
                }
            }
        } else {
            File[] listFiles = FolderList.getFolder_2b_2.listFiles();

            if (listFiles != null) {
                for (File file : listFiles) {
                    if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("log")) {
                        String logFilesName = file.getName().substring(0, 16);

                        if (logFilesName.equalsIgnoreCase(dateFormatter.format(date) + "-chat_")) {
                            String index = file.getName().split("_")[3].split("\\.")[0];

                            fileMap.put(Integer.parseInt(index), file);
                        }
                    }
                }
            }
        }
        return fileMap.get(fileMap.size());
    }

    public static void add(String defaultState, String playerName, String messages) throws FileNotFoundException {
        Scanner scanner = new Scanner(ChatLogger.getChatLog());
        FileOutputStream fileOutputStream = new FileOutputStream(ChatLogger.getChatLog(), true);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        try {
            if (scanner.hasNextLine()) {
                bufferedWriter.newLine();
            }

            bufferedWriter.write("[" + dateFormat.format(date) + "] " + defaultState + playerName + " > " + messages);

            scanner.close();
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}