package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private TrieNode root = new TrieNode();

    //敏感词的替代
    @Value("${text.replace}")
    private String REPLACEMENT;

    //初始化
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive_words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    //将敏感词进前缀树中
    public void addKeyword(String keyword) {
        if(StringUtils.isBlank(keyword))    return;
        TrieNode temNode = root;
        for (int i = 0; i < keyword.length(); i++) {
            char cur = keyword.charAt(i);//获取当前字符
            TrieNode childNode = temNode.getChildNode(cur);//从当前节点的孩子节点中获取该字符节点
            if (childNode == null) {//不存在该字符节点
                childNode = new TrieNode();//创建一个新的节点
                temNode.setChildren(cur, childNode);//添加节点
            }
            temNode = childNode;//指向当前字符的节点  即当前节点的孩子节点
            //设置结束标志
            if (i == keyword.length() - 1) {
                temNode.setEnd(true);
            }
        }
    }

     /**   参数为需要过滤的文本
     *   返回值为过滤处理后的文本
     * */
     public String filter(String text){
         if(StringUtils.isBlank(text)){
             return null;
         }
         // 指针1
         TrieNode tempNode = root;
         // 指针2
         int begin = 0;
         // 指针3
         int position = 0;
         // 结果
         StringBuilder sb = new StringBuilder();

         while(begin < text.length()){
             if(position < text.length()) {
                 Character c = text.charAt(position);

                 // 跳过符号
                 if (isSymbol(c)) {
                     if (tempNode == root) {
                         begin++;
                         sb.append(c);
                     }
                     position++;
                     continue;
                 }

                 // 检查下级节点
                 tempNode = tempNode.getChildNode(c);
                 if (tempNode == null) {
                     // 以begin开头的字符串不是敏感词
                     sb.append(text.charAt(begin));
                     // 进入下一个位置
                     position = ++begin;
                     // 重新指向根节点
                     tempNode = root;
                 }
                 // 发现敏感词
                 else if (tempNode.isEnd()) {
                     sb.append(REPLACEMENT);
                     begin = ++position;
                     tempNode = root;
                 }
                 // 检查下一个字符
                 else {
                     position++;
                 }
             }
             // position遍历越界仍未匹配到敏感词
             else{
                 sb.append(text.charAt(begin));
                 position = ++begin;
                 tempNode = root;
             }
         }
         return sb.toString();
     }

    // 判断是否为特殊符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        //c不为字母数字  且  不是东南亚文字
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树结构
    private class TrieNode {
        //判断当前节点是否为敏感词汇终点  即叶子节点 只不过我们这里直接手动标记
        private boolean isEnd = false;

        //存放孩子节点   key为存放的字符   value为对应的节点
        private Map<Character, TrieNode> children = new HashMap<>();

        //设置当前节点是否为叶子节点 敏感词结尾
        public void setEnd(boolean end) {
            isEnd = end;
        }

        //判断当前节点是否为敏感词结尾
        public boolean isEnd() {
            return isEnd;
        }

        //获取当前节点指定的孩子节点
        public TrieNode getChildNode(Character c) {
            return children.get(c);
        }

        //存放当前节点的孩子节点
        public void setChildren(Character c, TrieNode node) {
            children.put(c, node);
        }
    }

}
