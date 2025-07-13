package com.ijson.config.base;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.ijson.config.base.ConfigConstants.UTF8;

/**
 * @author cuiyongxu
 */
public class Config extends Properties {

    public static final Logger log = LoggerFactory.getLogger(Config.class);

    private volatile boolean parsed = false;
    private volatile String contentHash;
    private byte[] content;

    /**
     * 计算内容哈希值，用于判断内容是否变化
     * 
     * @param content 内容字节数组
     * @return 哈希值字符串
     */
    private String calculateContentHash(byte[] content) {
        if (content == null || content.length == 0) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(content);
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // 降级到简单哈希
            return String.valueOf(Arrays.hashCode(content));
        }
    }

    public synchronized byte[] getContent() {
        if (content == null) {
            Map<String, String> m = getAll();
            if (m.isEmpty()) {
                content = new byte[0];
            } else {
                // 预估容量，减少StringBuilder扩容次数
                int estimatedSize = m.size() * 32; // 估算每个键值对平均32字符
                StringBuilder sbd = new StringBuilder(estimatedSize);
                for (Map.Entry<String, String> entry : m.entrySet()) {
                    sbd.append(entry.getKey()).append('=')
                            .append(entry.getValue()).append('\n');
                }
                content = sbd.toString().getBytes(UTF8);
            }
        }
        return content;
    }

    public void copyOf(String s) {
        copyOf(s.getBytes(UTF8));
    }

    public void copyOf(byte[] content) {
        String newHash = calculateContentHash(content);
        if (!Objects.equals(this.contentHash, newHash)) {
            this.content = content;
            this.contentHash = newHash;
            this.parsed = false;
        }
    }

    @Override
    public void copyOf(Map<String, String> m) {
        super.copyOf(m);
        resetContent();
    }

    @Override
    public void copyOf(java.util.Properties props) {
        super.copyOf(props);
        resetContent();
    }

    @Override
    public Properties putAll(Map<String, String> items) {
        super.putAll(items);
        resetContent();
        return this;
    }

    @Override
    public Properties putAll(java.util.Properties props) {
        super.putAll(props);
        resetContent();
        return this;
    }

    private void resetContent() {
        parsed = true;
        this.content = null;
    }

    private synchronized void parse() {
        if (!parsed && content != null) {
            // 预估Map容量，减少扩容次数
            Map<String, String> m = Maps.newLinkedHashMapWithExpectedSize(32);
            String txt = new String(content, UTF8);

            // 优化字符串处理，直接按行分割
            parseConfigContent(txt, m);
            super.copyOf(m);
            parsed = true;
        }
    }

    /**
     * 解析配置内容
     * 
     * @param txt 配置文本
     * @param m   存储解析结果的Map
     */
    private void parseConfigContent(String txt, Map<String, String> m) {
        String[] lines = txt.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                continue;
            }

            int pos = line.indexOf('=');
            if (pos != -1) {
                String key = line.substring(0, pos).trim();
                String value = pos + 1 < line.length() ? line.substring(pos + 1).trim() : "";

                try {
                    m.put(key, unEscapeJava(value));
                } catch (Exception e) {
                    log.error("Cannot escape value for key '{}': {}", key, value, e);
                    // 即使转义失败，也保存原始值
                    m.put(key, value);
                }
            }
        }
    }

    /**
     * copyFrom StringEscapeUtils.unescapeJava
     * 
     * @param value
     * @return
     */
    private String unEscapeJava(String value) {
        if (value == null || value.length() == 0) {
            return value;
        }
        StringBuilder buf = null;
        int len = value.length();
        int len1 = len - 1;
        for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);
            if (ch == '\\' && i < len1) {
                int j = i;
                i++;
                ch = value.charAt(i);
                switch (ch) {
                    case '\\':
                        ch = '\\';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'u':
                    case 'U':
                        ch = (char) Integer.parseInt(value.substring(i + 1, i + 5), 16);
                        i = i + 4;
                        break;
                    default:
                        j--;
                }
                if (buf == null) {
                    buf = new StringBuilder(len);
                    if (j > 0) {
                        buf.append(value.substring(0, j));
                    }
                }
                buf.append(ch);
            } else if (buf != null) {
                buf.append(ch);
            }
        }
        if (buf != null) {
            return buf.toString();
        }
        return value;
    }

    @Override
    public String get(String key) {
        if (!parsed) {
            parse();
        }
        return super.get(key);
    }

    @Override
    public Map<String, String> getAll() {
        if (!parsed) {
            parse();
        }
        return super.getAll();
    }

    public String getString() {
        return new String(getContent(), UTF8);
    }

    public String getString(Charset charset) {
        return new String(getContent(), charset);
    }

    public List<String> getLines() {
        return getLines(UTF8, true);
    }

    public List<String> getLines(Charset charset) {
        return lines(new String(getContent(), charset), true);
    }

    public List<String> getLines(Charset charset, boolean removeComment) {
        return lines(new String(getContent(), charset), removeComment);
    }

    private List<String> lines(String s, boolean removeComment) {
        List<String> raw = Splitter.on('\n').trimResults().omitEmptyStrings().splitToList(s);
        if (!removeComment) {
            return raw;
        }

        List<String> clean = Lists.newArrayList();
        for (String i : raw) {
            if (i.charAt(0) == '#' || i.startsWith("//")) {
                continue;
            }
            clean.add(i);
        }
        return clean;
    }
}
