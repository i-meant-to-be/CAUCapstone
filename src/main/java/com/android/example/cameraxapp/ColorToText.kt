package com.android.example.cameraxapp

import java.util.Arrays
import java.util.stream.Collectors
import kotlin.math.abs

object ColorToText {
    @JvmStatic
    fun main(args: Array<String>) {

        //rgb 예시
        val r = 0xaa
        val g = 0xff
        val b = 0x22
        println(analyzer(r,g,b))
    }

    //rgb값에 따라 가장 가까운 색상 선택하여 이름 반환
    private fun colorSortRgb(table: Array<Array<String?>>, r: Int, g: Int, b: Int): String? {
        val diff = IntArray(table.size)

        //대표 색상의 table을 정수로 변환하여 저장
        for (j in table.indices) {
            var tempR = 0
            var tempG = 0
            var tempB = 0
            try {
                tempR = table[j][1]!!.toInt(16)
                tempG = table[j][2]!!.toInt(16)
                tempB = table[j][3]!!.toInt(16)
            } catch (e: NumberFormatException) {
                println("정수변환 오류")
            }

            //근사 색깔 결정 가중치 조절
            diff[j] =   ( abs(tempR - r) * abs(tempR - r)
                        + abs(tempG - g) * abs(tempG - g)
                        + abs(tempB - b) * abs(tempB - b)
                        )
        }
        val minIndex = diff.indices.minByOrNull { diff[it] }
        return table[minIndex!!][0]
    }
    //hsv값에 따라 가장 가까운 색상 선택하여 이름 반환
    private fun colorSortHsv(table: Array<Array<String?>>, r: Int, g: Int, b: Int) : String?{
        val colorTableHsv = Array(table.size){FloatArray(3) }
        val diff = DoubleArray(table.size)


        val hsv = rgbToHsv(r, g, b)

        //table rgb값을 hsv값으로 변경하여 저장
        for( i in table.indices) {
           colorTableHsv[i] = rgbToHsv(table[i][1]!!.toInt(16),table[i][2]!!.toInt(16),table[i][3]!!.toInt(16))
        }

        for (j in table.indices) {
            val diffH = abs(hsv[0] - colorTableHsv[j][0])/360
            val diffS = abs(hsv[1] - colorTableHsv[j][1])
            val diffV = abs(hsv[2] - colorTableHsv[j][2])

            //근사 색깔 결정 가중치 조절
            diff[j] = (   diffH * diffH * 4
                        + diffS
                        + diffV
                    ).toDouble()
        }

        val minIndex = diff.indices.minByOrNull { diff[it] }
        return table[minIndex!!][0]
    }

    //rgb값을 hsv값으로 변환
    fun rgbToHsv(red: Int, green: Int, blue: Int): FloatArray {
        val floatArray = FloatArray(3)

        val r = red / 255.0
        val g = green / 255.0
        val b = blue / 255.0

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)

        var h: Float = 0f
        var s: Float = 0f
        val v: Float = max.toFloat()

        val delta = max - min

        if (max != 0.0) {
            s = (delta / max).toFloat()
        } else {
            // r = g = b = 0
            s = 0f
            h = -1f
            floatArray[0] = h
            floatArray[1] = s
            floatArray[2] = v
            return floatArray
        }

        if (r == max) {
            h = ((g - b) / delta).toFloat()
        } else if (g == max) {
            h = (2 + (b - r) / delta).toFloat()
        } else {
            h = (4 + (r - g) / delta).toFloat()
        }

        h *= 60
        if (h < 0) h += 360

        floatArray[0] = h
        floatArray[1] = s
        floatArray[2] = v
        return floatArray
    }

    fun analyzer(r: Int, g: Int, b: Int) : String?{

        //대표 색상 table [한국산업표준 색이름-계통색) : 203가지]
        val colorTableKor: Array<Array<String?>> = arrayOf( arrayOf("빨강", "BB", "46", "42"),
                                                            arrayOf("선명한 빨강", "BF", "3B", "3C"),
                                                            arrayOf("밝은 빨강", "DD", "53", "4A"),
                                                            arrayOf("진한 빨강(진빨강)", "99", "3E", "41"),
                                                            arrayOf("흐린 빨강", "B0", "6F", "68"),
                                                            arrayOf("탁한 빨강", "93", "5B", "55"),
                                                            arrayOf("어두운 빨강", "63", "3D", "43"),
                                                            arrayOf("회적색", "92", "7A", "78"),
                                                            arrayOf("어두운 회적색", "68", "53", "51"),
                                                            arrayOf("검은 빨강", "54", "45", "46"),
                                                            arrayOf("주황", "F2", "77", "3D"),
                                                            arrayOf("선명한 주황", "F1", "78", "45"),
                                                            arrayOf("밝은 주황", "FF", "90", "58"),
                                                            arrayOf("진한 주황(진주황)", "DA", "67", "30"),
                                                            arrayOf("흐린 주황", "E0", "80", "5B"),
                                                            arrayOf("탁한 주황", "C9", "91", "76"),
                                                            arrayOf("빨간 주황", "DB", "5D", "3B"),
                                                            arrayOf("선명한 빨간 주황", "E1", "56", "37"),
                                                            arrayOf("밝은 빨간 주황", "F5", "6E", "48"),
                                                            arrayOf("탁한 빨간 주황", "AC", "70", "62"),
                                                            arrayOf("노란 주황", "FF", "98", "2E"),
                                                            arrayOf("선명한 노란 주황", "FF", "99", "13"),
                                                            arrayOf("밝은 노란 주황", "FF", "B1", "2B"),
                                                            arrayOf("진한 노란 주황", "DB", "86", "28"),
                                                            arrayOf("연한 노란 주황", "FF", "B6", "6A"),
                                                            arrayOf("흐린 노란 주황", "FA", "BE", "93"),
                                                            arrayOf("탁한 노란 주황", "C2", "8B", "69"),
                                                            arrayOf("노랑", "FF", "D1", "0D"),
                                                            arrayOf("진한 노랑(진노랑)", "FF", "BB", "14"),
                                                            arrayOf("연한 노랑(연노랑)", "FC", "DA", "94"),
                                                            arrayOf("흐린 노랑", "E4", "C7", "7E"),
                                                            arrayOf("흰 노랑", "F3", "E6", "C7"),
                                                            arrayOf("회황색", "DB", "CC", "B0"),
                                                            arrayOf("밝은 회황색", "E5", "D7", "BA"),
                                                            arrayOf("연두", "94", "C0", "56"),
                                                            arrayOf("선명한 연두", "77", "A8", "32"),
                                                            arrayOf("밝은 연두", "AB", "D6", "64"),
                                                            arrayOf("진한 연두", "64", "8C", "35"),
                                                            arrayOf("연한 연두", "B8", "D3", "8F"),
                                                            arrayOf("흐린 연두", "A0", "BB", "7F"),
                                                            arrayOf("탁한 연두", "8A", "A2", "69"),
                                                            arrayOf("노란 연두", "D8", "C9", "49"),
                                                            arrayOf("선명한 노란 연두", "E2", "CD", "26"),
                                                            arrayOf("밝은 노란 연두", "EE", "DA", "51"),
                                                            arrayOf("진한 노란 연두", "C6", "B1", "3C"),
                                                            arrayOf("연한 노란 연두", "FF", "B8", "A2"),
                                                            arrayOf("흐린 노란 연두", "E6", "A1", "8F"),
                                                            arrayOf("탁한 노란 연두", "C1", "83", "75"),
                                                            arrayOf("녹연두", "65", "A8", "54"),
                                                            arrayOf("선명한 녹연두", "5B", "AC", "49"),
                                                            arrayOf("밝은 녹연두", "7F", "C2", "6A"),
                                                            arrayOf("연한 녹연두", "AD", "D7", "97"),
                                                            arrayOf("흐린 녹연두", "91", "BD", "86"),
                                                            arrayOf("탁한 녹연두", "7B", "A5", "70"),
                                                            arrayOf("흰 연두", "E0", "E9", "CB"),
                                                            arrayOf("회연두", "96", "A0", "8A"),
                                                            arrayOf("밝은 회연두", "C6", "D1", "B5"),
                                                            arrayOf("초록", "24", "7C", "4D"),
                                                            arrayOf("선명한 초록", "05", "96", "51"),
                                                            arrayOf("밝은 초록", "36", "93", "5B"),
                                                            arrayOf("진한 초록(진초록)", "3C", "62", "49"),
                                                            arrayOf("연한 초록(연초록)", "95", "DA", "AF"),
                                                            arrayOf("흐린 초록", "6A", "AA", "87"),
                                                            arrayOf("탁한 초록", "4A", "78", "59"),
                                                            arrayOf("어두운 초록", "3E", "4E", "43"),
                                                            arrayOf("흰 초록", "D2", "ED", "D5"),
                                                            arrayOf("회녹색", "73", "87", "7A"),
                                                            arrayOf("밝은 회녹색", "BC", "D0", "BC"),
                                                            arrayOf("어두운 회녹색", "52", "5D", "53"),
                                                            arrayOf("검은 초록", "45", "4E", "48"),
                                                            arrayOf("청록", "00", "6B", "70"),
                                                            arrayOf("밝은 청록", "00", "97", "9C"),
                                                            arrayOf("진한 청록", "21", "55", "5A"),
                                                            arrayOf("연한 청록", "66", "C4", "C3"),
                                                            arrayOf("흐린 청록", "6F", "A6", "A5"),
                                                            arrayOf("탁한 청록", "43", "77", "79"),
                                                            arrayOf("어두운 청록", "34", "50", "52"),
                                                            arrayOf("흰 청록", "CE", "EB", "E6"),
                                                            arrayOf("회청록", "70", "88", "89"),
                                                            arrayOf("밝은 회청록", "9C", "B8", "B7"),
                                                            arrayOf("어두운 회청록", "49", "5E", "5B"),
                                                            arrayOf("검은 청록", "3E", "4C", "4E"),
                                                            arrayOf("파랑", "0F", "7C", "A8"),
                                                            arrayOf("선명한 파랑", "00", "8C", "C3"),
                                                            arrayOf("밝은 파랑", "4A", "A8", "D8"),
                                                            arrayOf("진한 파랑(진파랑)", "34", "4F", "65"),
                                                            arrayOf("연한 파랑(연파랑)", "A6", "D3", "E9"),
                                                            arrayOf("흐린 파랑", "75", "A6", "C0"),
                                                            arrayOf("탁한 파랑", "4A", "76", "91"),
                                                            arrayOf("어두운 파랑", "3A", "4E", "5B"),
                                                            arrayOf("흰 파랑", "DF", "E9", "ED"),
                                                            arrayOf("회청색", "7A", "87", "90"),
                                                            arrayOf("밝은 회청색", "AB", "B6", "BC"),
                                                            arrayOf("어두운 회청색", "52", "5E", "66"),
                                                            arrayOf("검은 파랑", "44", "4E", "56"),
                                                            arrayOf("남색", "41", "4A", "67"),
                                                            arrayOf("밝은 남색", "44", "5C", "91"),
                                                            arrayOf("흐린 남색", "4D", "5B", "7B"),
                                                            arrayOf("어두운 남색", "46", "4D", "5E"),
                                                            arrayOf("회남색", "69", "6F", "7A"),
                                                            arrayOf("검은 남색", "48", "4B", "54"),
                                                            arrayOf("보라", "66", "55", "81"),
                                                            arrayOf("선명한 보라", "7A", "63", "97"),
                                                            arrayOf("밝은 보라", "89", "77", "AD"),
                                                            arrayOf("진한 보라(진보라)", "55", "44", "66"),
                                                            arrayOf("연한 보라(연보라)", "BB", "AB", "D3"),
                                                            arrayOf("흐린 보라", "A6", "99", "BB"),
                                                            arrayOf("탁한 보라", "76", "67", "89"),
                                                            arrayOf("어두운 보라", "4F", "46", "58"),
                                                            arrayOf("흰 보라", "EA", "E7", "EC"),
                                                            arrayOf("회보라", "86", "82", "8E"),
                                                            arrayOf("밝은 회보라", "B7", "B3", "BD"),
                                                            arrayOf("어두운 회보라", "62", "5D", "6A"),
                                                            arrayOf("검은 보라", "4C", "47", "51"),
                                                            arrayOf("자주", "8B", "42", "5F"),
                                                            arrayOf("선명한 자주", "AB", "50", "71"),
                                                            arrayOf("밝은 자주", "CD", "5D", "85"),
                                                            arrayOf("진한 자주", "68", "3D", "51"),
                                                            arrayOf("연한 자주", "BB", "6C", "85"),
                                                            arrayOf("흐린 자주", "A9", "76", "83"),
                                                            arrayOf("탁한 자주", "7C", "4D", "5E"),
                                                            arrayOf("어두운 자주", "5B", "44", "4E"),
                                                            arrayOf("빨간 자주(적자색)", "8B", "41", "56"),
                                                            arrayOf("진한 적자색", "6D", "3E", "4D"),
                                                            arrayOf("탁한 적자색", "7B", "4C", "58"),
                                                            arrayOf("어두운 적자색", "5D", "45", "4D"),
                                                            arrayOf("회자주", "92", "82", "86"),
                                                            arrayOf("어두운 회자주", "66", "55", "5A"),
                                                            arrayOf("검은 자주", "54", "46", "4B"),
                                                            arrayOf("분홍", "E9", "9E", "A8"),
                                                            arrayOf("진한 분홍(진분홍)", "DB", "82", "91"),
                                                            arrayOf("연한 분홍(연분홍)", "F6", "BA", "BF"),
                                                            arrayOf("흐린 분홍", "DD", "A3", "AB"),
                                                            arrayOf("탁한 분홍", "C3", "8C", "94"),
                                                            arrayOf("노란 분홍", "FF", "94", "77"),
                                                            arrayOf("진한 노란 분홍", "E7", "7A", "62"),
                                                            arrayOf("연한 노란 분홍", "FF", "B8", "A2"),
                                                            arrayOf("흐린 노란 분홍", "E6", "A1", "8F"),
                                                            arrayOf("탁한 노란 분홍", "C1", "83", "75"),
                                                            arrayOf("흰 분홍", "F5", "E6", "E3"),
                                                            arrayOf("회분홍", "C2", "B1", "B3"),
                                                            arrayOf("밝은 회분홍", "DA", "C8", "C9"),
                                                            arrayOf("자줏빛 분홍", "E7", "9E", "B9"),
                                                            arrayOf("진한 자줏빛 분홍", "D2", "84", "A3"),
                                                            arrayOf("연한 자줏빛 분홍", "F2", "C0", "D1"),
                                                            arrayOf("흐린 자줏빛 분홍", "D9", "A8", "B8"),
                                                            arrayOf("탁한 자줏빛 분홍", "BC", "8D", "9E"),
                                                            arrayOf("갈색", "97", "5A", "3E"),
                                                            arrayOf("밝은 갈색", "CB", "6B", "31"),
                                                            arrayOf("진한 갈색", "78", "50", "3E"),
                                                            arrayOf("연한 갈색", "AA", "70", "4A"),
                                                            arrayOf("흐린 갈색", "A4", "7A", "68"),
                                                            arrayOf("탁한 갈색", "8C", "63", "54"),
                                                            arrayOf("어두운 갈색", "5C", "44", "3B"),
                                                            arrayOf("빨간 갈색(적갈색)", "8B", "43", "3C"),
                                                            arrayOf("밝은 적갈색", "A7", "55", "44"),
                                                            arrayOf("진한 적갈색", "61", "41", "3F"),
                                                            arrayOf("흐린 적갈색", "96", "5C", "51"),
                                                            arrayOf("탁한 적갈색", "7B", "4D", "47"),
                                                            arrayOf("어두운 적갈색", "58", "43", "42"),
                                                            arrayOf("노란 갈색(황갈색)", "AE", "77", "34"),
                                                            arrayOf("밝은 황갈색", "D8", "82", "31"),
                                                            arrayOf("연한 황갈색", "C9", "8C", "4F"),
                                                            arrayOf("흐린 황갈색", "B5", "93", "75"),
                                                            arrayOf("탁한 황갈색", "A7", "7A", "4D"),
                                                            arrayOf("녹갈색", "79", "6D", "3D"),
                                                            arrayOf("밝은 녹갈색", "99", "89", "3C"),
                                                            arrayOf("흐린 녹갈색", "92", "88", "5A"),
                                                            arrayOf("탁한 녹갈색", "74", "6D", "48"),
                                                            arrayOf("어두운 녹갈색", "5D", "5A", "3F"),
                                                            arrayOf("회갈색", "92", "7D", "75"),
                                                            arrayOf("어두운 회갈색", "69", "55", "4E"),
                                                            arrayOf("검은 갈색(흑갈색)", "54", "48", "43"),
                                                            arrayOf("하양", "F2", "F3", "F0"),
                                                            arrayOf("노란 하양", "EA", "E3", "D4"),
                                                            arrayOf("초록빛 하양", "DD", "ED", "DF"),
                                                            arrayOf("파란 하양", "E0", "E8", "E9"),
                                                            arrayOf("보랏빛 하양", "E9", "E7", "E8"),
                                                            arrayOf("분홍빛 하양", "EF", "E5", "E3"),
                                                            arrayOf("회색", "81", "83", "83"),
                                                            arrayOf("밝은 회색", "B2", "B5", "B3"),
                                                            arrayOf("어두운 회색", "5F", "62", "61"),
                                                            arrayOf("빨간 회색(적회색)", "8D", "83", "82"),
                                                            arrayOf("어두운 적회색", "5F", "53", "53"),
                                                            arrayOf("노란 회색(황회색)", "D4", "CB", "BB"),
                                                            arrayOf("초록빛 회색(녹회색)", "7E", "86", "81"),
                                                            arrayOf("밝은 녹회색", "C2", "CF", "C4"),
                                                            arrayOf("어두운 녹회색", "55", "5D", "59"),
                                                            arrayOf("파란 회색(청회색)", "7F", "87", "8A"),
                                                            arrayOf("밝은 청회색", "AD", "B5", "B7"),
                                                            arrayOf("어두운 청회색", "57", "5C", "61"),
                                                            arrayOf("보랏빛 회색", "86", "84", "8A"),
                                                            arrayOf("밝은 보랏빛 회색", "B4", "B3", "B7"),
                                                            arrayOf("어두운 보랏빛 회색", "5B", "59", "60"),
                                                            arrayOf("분홍빛 회색", "BB", "B2", "B3"),
                                                            arrayOf("갈회색", "8E", "81", "7D"),
                                                            arrayOf("어두운 갈회색", "61", "58", "55"),
                                                            arrayOf("검정", "3B", "3B", "3B"),
                                                            arrayOf("빨간 검정", "4E", "47", "47"),
                                                            arrayOf("초록빛 검정", "47", "4B", "49"),
                                                            arrayOf("파란 검정", "47", "4C", "4F"),
                                                            arrayOf("보랏빛 검정", "4B", "49", "4D"),
                                                            arrayOf("갈흑색", "4D", "49", "48")
                                                        )
        //대표 색상 table [CSS color Module lv.3 (HTML Colors) : 147가지]
        val colorTableCss: Array<Array<String?>> = arrayOf( arrayOf("aliceblue", "F0", "F8", "FF"),
                                                            arrayOf("antiquewhite", "FA", "EB", "D7"),
                                                            arrayOf("aqua", "00", "FF", "FF"),
                                                            arrayOf("aquamarine", "7F", "FF", "D4"),
                                                            arrayOf("azure", "F0", "FF", "FF"),
                                                            arrayOf("beige", "F5", "F5", "DC"),
                                                            arrayOf("bisque", "FF", "E4", "C4"),
                                                            arrayOf("black", "00", "00", "00"),
                                                            arrayOf("blanchedalmond", "FF", "EB", "CD"),
                                                            arrayOf("blue", "00", "00", "FF"),
                                                            arrayOf("blueviolet", "8A", "2B", "E2"),
                                                            arrayOf("brown", "A5", "2A", "2A"),
                                                            arrayOf("burlywood", "DE", "B8", "87"),
                                                            arrayOf("cadetblue", "5F", "9E", "A0"),
                                                            arrayOf("chartreuse", "7F", "FF", "00"),
                                                            arrayOf("chocolate", "D2", "69", "1E"),
                                                            arrayOf("coral", "FF", "7F", "50"),
                                                            arrayOf("cornflowerblue", "64", "95", "ED"),
                                                            arrayOf("cornsilk", "FF", "F8", "DC"),
                                                            arrayOf("crimson", "DC", "14", "3C"),
                                                            arrayOf("cyan", "00", "FF", "FF"),
                                                            arrayOf("darkblue", "00", "00", "8B"),
                                                            arrayOf("darkcyan", "00", "8B", "8B"),
                                                            arrayOf("darkgoldenrod", "B8", "86", "0B"),
                                                            arrayOf("darkgray", "A9", "A9", "A9"),
                                                            arrayOf("darkgreen", "00", "64", "00"),
                                                            arrayOf("darkgrey", "A9", "A9", "A9"),
                                                            arrayOf("darkkhaki", "BD", "B7", "6B"),
                                                            arrayOf("darkmagenta", "8B", "00", "8B"),
                                                            arrayOf("darkolivegreen", "55", "6B", "2F"),
                                                            arrayOf("darkorange", "FF", "8C", "00"),
                                                            arrayOf("darkorchid", "99", "32", "CC"),
                                                            arrayOf("darkred", "8B", "00", "00"),
                                                            arrayOf("darksalmon", "E9", "96", "7A"),
                                                            arrayOf("darkseagreen", "8F", "BC", "8F"),
                                                            arrayOf("darkslateblue", "48", "3D", "8B"),
                                                            arrayOf("darkslategray", "2F", "4F", "4F"),
                                                            arrayOf("darkslategrey", "2F", "4F", "4F"),
                                                            arrayOf("darkturquoise", "00", "CE", "D1"),
                                                            arrayOf("darkviolet", "94", "00", "D3"),
                                                            arrayOf("deeppink", "FF", "14", "93"),
                                                            arrayOf("deepskyblue", "00", "BF", "FF"),
                                                            arrayOf("dimgray", "69", "69", "69"),
                                                            arrayOf("dimgrey", "69", "69", "69"),
                                                            arrayOf("dodgerblue", "1E", "90", "FF"),
                                                            arrayOf("firebrick", "B2", "22", "22"),
                                                            arrayOf("floralwhite", "FF", "FA", "F0"),
                                                            arrayOf("forestgreen", "22", "8B", "22"),
                                                            arrayOf("fuchsia", "FF", "00", "FF"),
                                                            arrayOf("gainsboro", "DC", "DC", "DC"),
                                                            arrayOf("ghostwhite", "F8", "F8", "FF"),
                                                            arrayOf("gold", "FF", "D7", "00"),
                                                            arrayOf("goldenrod", "DA", "A5", "20"),
                                                            arrayOf("gray", "80", "80", "80"),
                                                            arrayOf("green", "00", "80", "00"),
                                                            arrayOf("greenyellow", "AD", "FF", "2F"),
                                                            arrayOf("grey", "80", "80", "80"),
                                                            arrayOf("honeydew", "F0", "FF", "F0"),
                                                            arrayOf("hotpink", "FF", "69", "B4"),
                                                            arrayOf("indianred", "CD", "5C", "5C"),
                                                            arrayOf("indigo", "4B", "00", "82"),
                                                            arrayOf("ivory", "FF", "FF", "F0"),
                                                            arrayOf("khaki", "F0", "E6", "8C"),
                                                            arrayOf("lavender", "E6", "E6", "FA"),
                                                            arrayOf("lavenderblush", "FF", "F0", "F5"),
                                                            arrayOf("lawngreen", "7C", "FC", "00"),
                                                            arrayOf("lemonchiffon", "FF", "FA", "CD"),
                                                            arrayOf("lightblue", "AD", "D8", "E6"),
                                                            arrayOf("lightcoral", "F0", "80", "80"),
                                                            arrayOf("lightcyan", "E0", "FF", "FF"),
                                                            arrayOf("lightgoldenrodyellow", "FA", "FA", "D2"),
                                                            arrayOf("lightgray", "D3", "D3", "D3"),
                                                            arrayOf("lightgreen", "90", "EE", "90"),
                                                            arrayOf("lightgrey", "D3", "D3", "D3"),
                                                            arrayOf("lightpink", "FF", "B6", "C1"),
                                                            arrayOf("lightsalmon", "FF", "A0", "7A"),
                                                            arrayOf("lightseagreen", "20", "B2", "AA"),
                                                            arrayOf("lightskyblue", "87", "CE", "FA"),
                                                            arrayOf("lightslategray", "77", "88", "99"),
                                                            arrayOf("lightslategrey", "77", "88", "99"),
                                                            arrayOf("lightsteelblue", "B0", "C4", "DE"),
                                                            arrayOf("lightyellow", "FF", "FF", "E0"),
                                                            arrayOf("lime", "00", "FF", "00"),
                                                            arrayOf("limegreen", "32", "CD", "32"),
                                                            arrayOf("linen", "FA", "F0", "E6"),
                                                            arrayOf("magenta", "FF", "00", "FF"),
                                                            arrayOf("maroon", "80", "00", "00"),
                                                            arrayOf("mediumaquamarine", "66", "CD", "AA"),
                                                            arrayOf("mediumblue", "00", "00", "CD"),
                                                            arrayOf("mediumorchid", "BA", "55", "D3"),
                                                            arrayOf("mediumpurple", "93", "70", "DB"),
                                                            arrayOf("mediumseagreen", "3C", "B3", "71"),
                                                            arrayOf("mediumslateblue", "7B", "68", "EE"),
                                                            arrayOf("mediumspringgreen", "00", "FA", "9A"),
                                                            arrayOf("mediumturquoise", "48", "D1", "CC"),
                                                            arrayOf("mediumvioletred", "C7", "15", "85"),
                                                            arrayOf("midnightblue", "19", "19", "70"),
                                                            arrayOf("mintcream", "F5", "FF", "FA"),
                                                            arrayOf("mistyrose", "FF", "E4", "E1"),
                                                            arrayOf("moccasin", "FF", "E4", "B5"),
                                                            arrayOf("navajowhite", "FF", "DE", "AD"),
                                                            arrayOf("navy", "00", "00", "80"),
                                                            arrayOf("oldlace", "FD", "F5", "E6"),
                                                            arrayOf("olive", "80", "80", "00"),
                                                            arrayOf("olivedrab", "6B", "8E", "23"),
                                                            arrayOf("orange", "FF", "A5", "00"),
                                                            arrayOf("orangered", "FF", "45", "00"),
                                                            arrayOf("orchid", "DA", "70", "D6"),
                                                            arrayOf("palegoldenrod", "EE", "E8", "AA"),
                                                            arrayOf("palegreen", "98", "FB", "98"),
                                                            arrayOf("paleturquoise", "AF", "EE", "EE"),
                                                            arrayOf("palevioletred", "DB", "70", "93"),
                                                            arrayOf("papayawhip", "FF", "EF", "D5"),
                                                            arrayOf("peachpuff", "FF", "DA", "B9"),
                                                            arrayOf("peru", "CD", "85", "3F"),
                                                            arrayOf("pink", "FF", "C0", "CB"),
                                                            arrayOf("plum", "DD", "A0", "DD"),
                                                            arrayOf("powderblue", "B0", "E0", "E6"),
                                                            arrayOf("purple", "80", "00", "80"),
                                                            arrayOf("red", "FF", "00", "00"),
                                                            arrayOf("rosybrown", "BC", "8F", "8F"),
                                                            arrayOf("royalblue", "41", "69", "E1"),
                                                            arrayOf("saddlebrown", "8B", "45", "13"),
                                                            arrayOf("salmon", "FA", "80", "72"),
                                                            arrayOf("sandybrown", "F4", "A4", "60"),
                                                            arrayOf("seagreen", "2E", "8B", "57"),
                                                            arrayOf("seashell", "FF", "F5", "EE"),
                                                            arrayOf("sienna", "A0", "52", "2D"),
                                                            arrayOf("silver", "C0", "C0", "C0"),
                                                            arrayOf("skyblue", "87", "CE", "EB"),
                                                            arrayOf("slateblue", "6A", "5A", "CD"),
                                                            arrayOf("slategray", "70", "80", "90"),
                                                            arrayOf("slategrey", "70", "80", "90"),
                                                            arrayOf("snow", "FF", "FA", "FA"),
                                                            arrayOf("springgreen", "00", "FF", "7F"),
                                                            arrayOf("steelblue", "46", "82", "B4"),
                                                            arrayOf("tan", "D2", "B4", "8C"),
                                                            arrayOf("teal", "00", "80", "80"),
                                                            arrayOf("thistle", "D8", "BF", "D8"),
                                                            arrayOf("tomato", "FF", "63", "47"),
                                                            arrayOf("turquoise", "40", "E0", "D0"),
                                                            arrayOf("violet", "EE", "82", "EE"),
                                                            arrayOf("wheat", "F5", "DE", "B3"),
                                                            arrayOf("white", "FF", "FF", "FF"),
                                                            arrayOf("whitesmoke", "F5", "F5", "F5"),
                                                            arrayOf("yellow", "FF", "FF", "00"),
                                                            arrayOf("yellowgreen", "9A", "CD", "32"),
                                                            )


        //hsv로 근사값 판별
//        return colorSortHsv(colorTableKor, r, g, b)
        //rgb로 근사값 판별
        return colorSortRgb(colorTableCss, r, g, b)
    }
}