package com.nowcoder.service;

import com.nowcoder.controller.QuestionController;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.swing.tree.TreeNode;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by LIU ZHAOZHI on 2017-6-7.
 */
@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
    @Override
    public void afterPropertiesSet() throws Exception{
        try{
            InputStream is = Thread.currentThread().
                    getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);
            String lineTxt;
            while((lineTxt = br.readLine()) != null){
                addWord(lineTxt.trim());
            }
            br.close();
            reader.close();
            is.close();
        }catch (Exception e){
            logger.error("读取敏感词文件失败"+e.getMessage());
        }

    }
    //增加关键词
    private void addWord(String lineTxt){
        TrieNode tempNode = rootNode;
        for(int i=0;i<lineTxt.length();++i){
            Character c = lineTxt.charAt(i);

            if(isSymbol(c)){

                continue;
            }
            TrieNode node = tempNode.getSubNode(c);

            if(node == null){
                node = new TrieNode();
                tempNode.addSubNode(c,node);
            }
            tempNode = node;

            if(i==lineTxt.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    private class TrieNode{
        //是不是关键词的结尾
        private boolean end = false;
        //当前节点下所有子节点
        private Map<Character, TrieNode> subNode = new HashMap<Character, TrieNode>();

        //向指定位置添加节点树
        public void addSubNode(Character key, TrieNode node){
            subNode.put(key, node);
        }

        //获取下个节点
        TrieNode getSubNode(Character key){
            return subNode.get(key);
        }

        boolean isKeywordEnd(){
            return end;
        }

        void setKeywordEnd(boolean end){
            this.end = end;
        }
    }

    private TrieNode rootNode = new TrieNode();

    //判断是否特殊符号
    private boolean isSymbol(char c){
        int ic = (int)c;
        //东亚文字（0x2E80~0x9FFF）
        return !CharUtils.isAsciiAlphanumeric(c) && (ic<0x2E80 || ic>0x9FFF);
    }

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return text;
        }

        StringBuilder result = new StringBuilder();

        String replacement = "***";
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;

        while(position<text.length()){
            char c = text.charAt(position);
            if(isSymbol(c)){
                if(tempNode == rootNode){
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            //当前节点的下一个节点
            tempNode = tempNode.getSubNode(c);

            if(tempNode==null){
               result.append(text.charAt(begin));
               position = begin+1;
               begin = position;
               tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                //发现敏感词
                result.append(replacement);
                position=position+1;
                begin=position;

            }else{
                ++position;
            }
        }
        //这里不懂
        //result.append(text.substring(begin));

        return result.toString();
    }

    public static void main(String[] args){
        SensitiveService ss = new SensitiveService();
        ss.addWord("色情");
        ss.addWord("赌博");
        System.out.println(ss.filter("hi你好色 情干很快就恢复和圣诞   节华发商都"));
    }
}
