package com.hemw.text;

/**
 * 用户进行文本内容比较
 * @author lysming
 */
import java.util.Arrays;
import java.util.List;

public class TextCompareUtil {
	public static String compareAsHtml(String before, String after) {
		List<TextDiff> diffs = TextComparer.compare(before, after);
		TextDiff[] diffArrays = new TextDiff[diffs.size()];
		Arrays.sort(diffs.toArray(diffArrays));
		int deleteIndex = 0,addIndex = 0;
		StringBuffer html = new StringBuffer();
		System.out.println(Arrays.asList(diffArrays));
		String content,keeped;
		for (TextDiff diff : diffArrays) {
			if (diff.getDiffType() == TextDiff.TYPE_DELETED) {
				if(deleteIndex<diff.getDiffStartIndex()&&diff.getDiffStartIndex()<before.length()){
					keeped = before.substring(deleteIndex, diff.getDiffStartIndex());
					System.out.println("remain : "+keeped);
					html.append(keeped);
					addIndex += diff.getDiffStartIndex()-deleteIndex;
					deleteIndex = diff.getDiffStartIndex();
				}
				content = before.substring(diff.getDiffStartIndex(), diff.getDiffStartIndex() + diff.getDiffLength());
				System.out.println("del : "+content+ "\t"+diff);
				html.append("<span class='delete'>" + content + "</span>");
				deleteIndex += diff.getDiffLength();
			} else {
				if(addIndex<diff.getDiffStartIndex()&&diff.getDiffStartIndex()<after.length()){
					keeped = after.substring(addIndex, diff.getDiffStartIndex());
					html.append(keeped);
					deleteIndex += diff.getDiffStartIndex()-addIndex;
					addIndex = diff.getDiffStartIndex();
				}
				content = after.substring(diff.getDiffStartIndex(), diff.getDiffStartIndex() + diff.getDiffLength());
				System.out.println("add : "+content +"\t"+diff);
				html.append("<span class='add'>" + content + "</span>");
				addIndex += diff.getDiffLength();
			}
		}
		if (addIndex < after.length()) {
			html.append(after.substring(addIndex));
		}
		/*if (index < before.length()) {
			html.append(before.substring(index));
		}*/

		return html.toString();
	}

	public static void main(String[] args) {
//		 String before = "这是原句，没有修改。该短句将被删除。此句将被删除。该短句将被保留。";
		String before = "新华社伦敦７月４日电（记者黄堃）最新一期英国《自然》杂志刊登研究论文说，一种被称为“ＩＳＬ１”的心脏祖细胞具有分化为心肌、心血管等主要心脏细胞的能力，这为心脏病的治疗带来福音。\r\n　　《自然》杂志刊登的这项由美国科研人员完成的研究说，他们最先在实验鼠身上发现了“ＩＳＬ１”心脏祖细胞，它可以分化为老鼠心脏中所有主要类型的细胞。最新研究又发现人体内也存在这种心脏祖细胞，并已证实可分化为心室肌细胞、平滑肌细胞和心血管内皮细胞等人类心脏中主要类型的细胞。\r\n　　祖细胞又称前体细胞，它居于干细胞和成体细胞之间。与能分化成各种类型细胞的干细胞不同，祖细胞的分化方向已比较确定，通常只能分化成特定类型的细胞。新发现的这种祖细胞可分化成多种主要的人类心脏细胞，称得上是“全能心脏祖细胞”。\r\n　　研究人员说，尽管这种细胞在实验鼠体内只能存在几天，但在人类胎儿中却能存在数周，这说明它在人类心脏形成过程中发挥着重要作用。今后也许可以通过植入这种“全能心脏祖细胞”，帮助治疗先天性心脏缺陷等心脏疾病。（完）";
		String after  = "新华社伦敦７月４日电（记者黄堃）最新一期英国《自然》杂志刊登研究论文说，一种被称为“ＩＳＬ１”的心脏祖细胞具有分化为心肌测试哈哈、心血管等主要心脏细胞的能力，这为心脏病的治疗带来福音。\r\n　　《自然》杂志刊登的这项由美国科研人员完成的研究说，他们最先在实验鼠身上发现了“ＩＳＬ１”心脏祖细胞，它可以分化为老鼠心脏中所有主要类型的细胞。最新研究又发现人体内也存在这种心脏祖细胞，并已证实可分化为心室肌细胞、平滑肌细胞和心血管内皮细胞等人类心脏中主要类型的细胞。\r\n　　祖细胞又称前成特定类型的细胞。新发现的这种祖细胞可分化成多种主要的人类心脏细胞，称得上是“全能心脏祖细胞”。\n　　研究人员说，尽管这种细胞在实验鼠体内只能存在几天，但在人类胎儿中却能存在数周，这说明它在人类心脏形成过程OK。（完）";
//		 String after  = "这是新的一句，经过修改。lysming。该短句将被保留。ok";
		System.out.println(compareAsHtml(before, after));
	}
}
