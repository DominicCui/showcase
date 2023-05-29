package edu.gatech.seclass.processfile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    // Empty Main class for compiling Individual Project.
    // DO NOT ALTER THIS CLASS or implement it.

    private static final Charset charset = StandardCharsets.UTF_8;

    public static void main(String[] args) throws IOException {

        List<String> option = new ArrayList<>();
        String keep = null;
        String target = null;
        String replace = null;
        String padding = null;

        if (args.length == 0){
            usage();
        } else {
            Path path;
            File file;

            try {
                path = Paths.get(args[args.length - 1]);
                file = new File(String.valueOf(path));
            }catch(InvalidPathException e){
                usage();return;
            }

            if (!file.isFile() || !file.exists() || file.isDirectory()) {
                usage();return;
            }

            String[] command = Arrays.copyOfRange(args, 0, args.length-1);

            for (int i = 0; i < command.length; i++) {
                if (Objects.equals(command[i], "-s")) {
                    option.add(command[i]);
                    if (i < command.length - 1)
                        keep = command[++i];
                    continue;
                }
                if (Objects.equals(command[i], "-r")) {
                    option.add(command[i]);
                    if (i < command.length - 2) {
                        target = command[++i];
                        replace = command[++i];
                    } else {
                        usage();return;
                    }
                    continue;
                }
                if (Objects.equals(command[i], "-p")) {
                    option.add(command[i]);
                    if (i < command.length - 1)
                        padding = command[++i];
                    continue;
                }
                if (command[i].charAt(0) == '-')
                    option.add(args[i]);
                else {
                    usage();return;
                }
            }

            if ( !validOptions(option) ||
                    (option.contains("-r") && (target == null || replace == null)) ||
                    (option.contains("-s") && keep == null) ||
                    (option.contains("-p") && padding == null)) {
                usage();return;
            }

            String content = Files.readString(path, charset);
            int lineSeparators = content.length() - content.replaceAll(System.lineSeparator(), "").length();
            List<String> modified = Arrays.asList(content.split(System.lineSeparator()));
            int lines = modified.size();
            if (lineSeparators < lines){
                modified = Files.readAllLines(path, charset);
            }

            if (option.contains("-s") && keep != null)
                modified = keep(modified, keep, option.contains("-i"));
            if (option.contains("-r"))
                replaceInsensitive(modified, target, replace, option.contains("-i"), option.contains("-g"));
            if (option.contains("-p") || option.contains("-n"))
                padding(modified, option.contains("-p"), padding, option.contains("-n"));

            String output = String.join(System.lineSeparator(), modified);
            if (option.contains("-f"))
                if (lineSeparators >= lines)
                    editInFile(output+System.lineSeparator(), file);
                else
                    editInFile(output, file);
            else
                if (lineSeparators >= lines)
                    System.out.println(output);
                else
                    System.out.print(output);
        }

    }

    // -g with -r only | -i with either -s or -r
    private static boolean validOptions(List<String> option){
        boolean valid = true;

        if (option.contains("-g") && !option.contains("-r"))
            return false;

        if (option.contains("-i"))
            valid = option.contains("-s") || option.contains("-r");

        return valid;
    }

    // -f | edit in file
    private static void editInFile(String content, File file) throws IOException {
        FileWriter fooWriter = new FileWriter(file, false);
        fooWriter.write(content);
        fooWriter.close();
    }

    // -s string | keep the line has the string
    private static List<String> keep(List<String> content, String keep, boolean i) {
        return content.stream()
                .filter(item -> i ? item.toLowerCase(Locale.ROOT).contains(keep.toLowerCase(Locale.ROOT)) : item.contains(keep))
                .collect(Collectors.toList());
    }

    // -r | -r -i | -r -g | -r -i -g
    private static List<String> replaceInsensitive(List<String> content, String target, String replace, boolean insensitive, boolean all) {
        for (int i = 0; i < content.size(); i++) {
            if (all && insensitive)
                while (content.get(i).toLowerCase(Locale.ROOT).contains(target.toLowerCase(Locale.ROOT)))
                    content.set(i, replace(content.get(i), target, replace));
            else if (insensitive)
                content.set(i, replace(content.get(i), target, replace));
            else if (all)
                content.set(i, content.get(i).replaceAll(target, replace));
            else
                content.set(i, content.get(i).replaceFirst(target, replace));
        }
        return content;
    }

    private static String replace(String content, String original, String replace) {
        if (content.toLowerCase(Locale.ROOT).contains(original.toLowerCase(Locale.ROOT))) {
            int index = content.toLowerCase(Locale.ROOT).indexOf(original.toLowerCase(Locale.ROOT));
            return content.replace(content.substring(index, index + original.length()), replace);
        } else {
            return content;
        }
    }

    // -n | -p | both
    private static List<String> padding(List<String> content, boolean p, String prefix, boolean n) {
        for (int i = 0; i < content.size(); i++) {
            StringBuilder padding = new StringBuilder();
            if (n)
                padding.append(i + 1).append(" ");
            if (p)
                padding.append(prefix);
            padding.append(content.get(i));
            content.set(i, padding.toString());
        }
        return content;
    }

    // usage
    private static void usage() {
        System.err.println("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE");
    }
}
