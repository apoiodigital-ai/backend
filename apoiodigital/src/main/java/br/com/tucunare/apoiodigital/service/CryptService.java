package br.com.tucunare.apoiodigital.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CryptService {

    private static final char[] PARTICULAR_KEY =
            "4>IÉÔôÚzN\"u;[BTc0Öb*YJçË\\qãèyöëúàjfÙ+omïÍ{Â^Hòvr=aÌwÊV`Ó8ù$tR6/)#]-!Û17%ó3(dAEÒX,k'S?Èh:Uì}5ei<sîÎÃ@~M2ÄÁäpFGí9POQgÜ|CD_ÀW&áxKnüÏZ.°êûÕLâõléÇ"
                    .toCharArray();

    private static final String ALL_CHARS =
            "abcdefghijklmnopqrstuvwxyzçABCDEFGHIJKLMNOPQRSTUVWXYZÇáàâãäéèêëíìîïóòôõöúùûüÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜ!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~1234567890°";

    public String shuffleString(String text) {
        List<Character> chars = text.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());

        Collections.shuffle(chars);

        StringBuilder builder = new StringBuilder(chars.size());
        for (char c : chars) {
            builder.append(c);
        }
        return builder.toString();
    }

    public String getPublicKey() {
        return shuffleString(ALL_CHARS);
    }

    private List<String> setGlossary(String publicKey) {

        boolean alternador = true;
        List<String> glossary = new ArrayList<>();
        int contador = 1;

        char[] publicKeyChars = publicKey.toCharArray();
        char[] baseChars = ALL_CHARS.toCharArray();

        for (char ignored : baseChars) {
            String codigo;

            if (alternador) {
                codigo = ""
                        + publicKeyChars[contador - 1]
                        + PARTICULAR_KEY[contador - 1]
                        + publicKeyChars[publicKeyChars.length - contador];
            } else {
                codigo = ""
                        + publicKeyChars[contador - 1]
                        + PARTICULAR_KEY[contador - 1]
                        + publicKeyChars[publicKeyChars.length - contador + 1];
            }

            glossary.add(codigo);
            contador++;
            alternador = !alternador;
        }

        // espaço em branco
        glossary.add(
                "" + publicKeyChars[5] + PARTICULAR_KEY[2] + publicKeyChars[publicKeyChars.length - 1]
        );

        return glossary;
    }

    public String encripty(String texto, String publicKey) {

        StringBuilder textoCriptografado = new StringBuilder();
        List<String> glossary = setGlossary(publicKey);
        String caracteresAuxiliares = ALL_CHARS + " ";

        for (char caractere : texto.toCharArray()) {
            int index = caracteresAuxiliares.indexOf(caractere);

            if (index < 0) {
                textoCriptografado.append("???");
            } else {
                textoCriptografado.append(
                        shuffleString(glossary.get(index))
                );
            }
        }

        return textoCriptografado.toString();
    }

    public String descrypt(String textoCriptografado, String publicKey) {

        List<String> glossary = setGlossary(publicKey);
        char[] caracteresAuxiliares = (ALL_CHARS + " ").toCharArray();

        int cursor = 0;
        StringBuilder textoDescriptografado = new StringBuilder();

        while (cursor + 2 < textoCriptografado.length()) {

            String bloco = ""
                    + textoCriptografado.charAt(cursor)
                    + textoCriptografado.charAt(cursor + 1)
                    + textoCriptografado.charAt(cursor + 2);

            if ("???".equals(bloco)) {
                textoDescriptografado.append("?");
                cursor += 3;
                continue;
            }

            char[] codigoOrdenado = bloco.toCharArray();
            Arrays.sort(codigoOrdenado);

            int indiceEncontrado = -1;

            for (int i = 0; i < glossary.size(); i++) {
                char[] aux = glossary.get(i).toCharArray();
                Arrays.sort(aux);

                if (Arrays.equals(aux, codigoOrdenado)) {
                    indiceEncontrado = i;
                    break;
                }
            }

            if (indiceEncontrado >= 0) {
                textoDescriptografado.append(caracteresAuxiliares[indiceEncontrado]);
            }

            cursor += 3;
        }

        return textoDescriptografado.toString();
    }
}
