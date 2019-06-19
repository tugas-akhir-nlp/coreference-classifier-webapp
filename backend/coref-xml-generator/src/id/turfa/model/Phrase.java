/*
Author: Muhammad Gilang Julian Suherik
 */

package id.turfa.model;

import java.util.ArrayList;
import java.util.List;

public class Phrase {
    private static int npcount = 1;
    public List<Phrase> childs;
    public String phrase;
    public String tag;

    public Phrase(String phrase, String tag, boolean doProcess) {
        this.phrase = phrase;
        this.tag = tag;
        childs = new ArrayList<>();
        if (doProcess) {
            if (containTag(this.phrase)) {
                process(this.phrase, 0);
            } else {
                this.phrase = this.phrase.substring(1, this.phrase.length() - 1);
            }
        }
    }

    public void process(String str, int startIdx) {
        int idx1 = startIdx;
        int idx2 = idx1;
        while (idx2 < str.length() - 1) {
            idx1 = str.indexOf("(", idx2) + 1;
            idx2 = str.indexOf(" ", idx1);
            if (idx1 < 0 || idx2 < 0) {
                break;
            }
            String tag = str.substring(idx1, idx2);
            idx1 = idx2 + 1;
            idx2 = getEndidx(str, idx1);
            if (idx1 > 0 && idx2 > 0) {
                String childPhrase = str.substring(idx1, idx2);
                childs.add(new Phrase(childPhrase, tag, true));
            } else {
                break;
            }
        }
    }

    public boolean containTag(String str) {
        if (str.indexOf('(') != str.lastIndexOf('(')) {
            return true;
        }
        return false;
    }

    public int getEndidx(String str, int curIdx) {
        int counter = 1;
        int idx = curIdx;
        while (counter > 0) {
            if (str.charAt(idx) == '(')
                counter++;
            else if (str.charAt(idx) == ')')
                counter--;
            idx++;
        }
        return idx - 1;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (childs.size() == 0 && (phrase.contains("*") || phrase.contains("&brr;") || phrase.contains("&brl;") || phrase.length() == 0)) {
            return "";
        }

        if (phrase.equals(",") || phrase.equals(".") || phrase.equals("/")) {
            return phrase;
        }

        if ((tag.startsWith("NP") || tag.startsWith("PRP")) && !phrase.contains("(NP") && !phrase.contains("(PRP")) {
            if (tag.contains("SBJ")) {
                builder.append("<phrase type=\"np-sbj\" id=\"" + (npcount++) + "\">");
            } else {
                builder.append("<phrase type=\"np\" id=\"" + (npcount++) + "\">");
            }
        }
        if (childs.size() == 0) {
            builder.append(phrase.replace(" ", "\\" + tag + " ").replace("&", "&amp;") + "\\" + tag + " ");
        } else {
            for (int i = 0; i < childs.size(); i++) {
                Phrase p = childs.get(i);
                String phraseString = p.toString();
                if (phraseString.equals(",") || phraseString.equals("/") || phraseString.equals(".")) {
                    int idx = builder.lastIndexOf("\\");
                    if (idx != -1) {
                        builder.insert(idx, phraseString);
                    } else {
                        builder.append(phraseString);
                    }
                } else {
                    builder.append(phraseString);
                }
            }
        }
        if ((tag.startsWith("NP") || tag.startsWith("PRP")) && !phrase.contains("(NP") && !phrase.contains("(PRP")) {
            builder.append("</phrase>");
        }
        return builder.toString();
    }
}