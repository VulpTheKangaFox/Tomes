package com.vulp.tomes.enchantments;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EnchantClueHolder {

    private static final List<Pair<Enchantment, Integer>> empty = Collections.singletonList(new Pair<>(null, -1));

    public List<Pair<Enchantment, Integer>> slot1 = empty;
    public List<Pair<Enchantment, Integer>> slot2 = empty;
    public List<Pair<Enchantment, Integer>> slot3 = empty;

    public EnchantClueHolder() {

    }

    public void setData(int slot, List<Pair<Enchantment, Integer>> cluedPairingList) {
        if (slot == 0) {
            this.slot1 = cluedPairingList;
        } else if (slot == 1) {
            this.slot2 = cluedPairingList;
        } else if (slot == 2) {
            this.slot3 = cluedPairingList;
        }
    }

    public List<Pair<Enchantment, Integer>> getData(int slot) {
        if (slot == 0) {
            return this.slot1;
        } else if (slot == 1) {
            return this.slot2;
        } else if (slot == 2) {
            return this.slot3;
        }
        else return Collections.singletonList(new Pair<>(null, -1));
    }

    public static int[] encodeClues(EnchantClueHolder holder, int[] enchantClue) {
        List<Pair<Enchantment, Integer>> slot1 = holder.slot1;
        List<Pair<Enchantment, Integer>> slot2 = holder.slot2;
        List<Pair<Enchantment, Integer>> slot3 = holder.slot3;
        int a = slot1.size();
        int b = slot2.size();
        int c = slot3.size();
        int[] enchants = new int[a + b + c + 3];
        int[] levels = new int[a + b + c];
        int counter = 0;
        for (Pair<Enchantment, Integer> value : slot1) {
            enchants[counter] = Registry.ENCHANTMENT.getId(value.getFirst());
            counter++;
        }
        enchants[counter] = -5;
        counter++;
        for (Pair<Enchantment, Integer> pair : slot2) {
            enchants[counter] = Registry.ENCHANTMENT.getId(pair.getFirst());
            counter++;
        }
        enchants[counter] = -5;
        counter++;
        for (Pair<Enchantment, Integer> integerPair : slot3) {
            enchants[counter] = Registry.ENCHANTMENT.getId(integerPair.getFirst());
            counter++;
        }
        enchants[counter] = -5;
        counter = 0;
        for (Pair<Enchantment, Integer> enchantmentIntegerPair : slot1) {
            levels[counter] = enchantmentIntegerPair.getSecond();
            counter++;
        }
        for (Pair<Enchantment, Integer> enchantmentIntegerPair : slot2) {
            levels[counter] = enchantmentIntegerPair.getSecond();
            counter++;
        }
        for (Pair<Enchantment, Integer> enchantmentIntegerPair : slot3) {
            levels[counter] = enchantmentIntegerPair.getSecond();
            counter++;
        }
        int enchLength = enchants.length;
        int lvlLength = levels.length;
        int[] finalArray = new int[enchLength + lvlLength + 3];
        System.arraycopy(enchantClue, 0, finalArray, 0, 3);
        System.arraycopy(enchants, 0, finalArray, 3, enchLength);
        System.arraycopy(levels, 0, finalArray, enchLength + 3, lvlLength);
        return finalArray;
    }

    public static EnchantClueHolder decodeClues(int[] encodedArray) {
        int[] array = Arrays.copyOfRange(encodedArray, 3, encodedArray.length);
        int length1 = 0;
        int length2 = 0;
        int length3 = 0;
        int counter = 0;
        int selector = 0;
        for (int j : array) {
            if (j == -5) {
                if (selector == 0) {
                    length1 = counter;
                    counter = 0;
                } else if (selector == 1) {
                    length2 = counter - 1;
                    counter = 0;
                } else {
                    length3 = counter - 1;
                    break;
                }
                selector++;
            }
            counter++;
        }
        array = Arrays.stream(array).filter(i -> i != -5).toArray();
        int halfLength = array.length / 2;
        Enchantment[] enchantArray = new Enchantment[halfLength];
        int[] lvlArray = Arrays.copyOfRange(array, halfLength, array.length);
        for (int i = 0; i < halfLength; i++) {
            enchantArray[i] = Enchantment.getEnchantmentByID(array[i]);
        }
        EnchantClueHolder holder = new EnchantClueHolder();
        counter = 0;
        List<Pair<Enchantment, Integer>> finalList = new java.util.ArrayList<>(Collections.emptyList());
        for (int i = counter; i < length1; i++) {
            finalList.add(new Pair<>(enchantArray[counter], lvlArray[counter]));
            counter++;
        }
        holder.setData(0, finalList);
        finalList = new java.util.ArrayList<>(Collections.emptyList());
        for (int i = counter; i < length1 + length2; i++) {
            finalList.add(new Pair<>(enchantArray[counter], lvlArray[counter]));
            counter++;
        }
        holder.setData(1, finalList);
        finalList = new java.util.ArrayList<>(Collections.emptyList());
        for (int i = counter; i < length1 + length2 + length3; i++) {
            finalList.add(new Pair<>(enchantArray[counter], lvlArray[counter]));
            counter++;
        }
        holder.setData(2, finalList);
        return holder;
    }

}
