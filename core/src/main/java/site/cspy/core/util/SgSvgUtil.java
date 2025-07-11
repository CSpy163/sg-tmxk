package site.cspy.core.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SgSvgUtil {

    /**
     * 将SVG文件转换为PNG文件
     *
     * @param svgInput  输入SVG文件路径
     * @param pngOutput 输出PNG文件路径
     * @throws Exception 转换过程中的异常
     */
    public static void convertToPng(String svgInput, String pngOutput) throws Exception {
        // 创建PNG转码器
//        PNGTranscoder transcoder = new PNGTranscoder();
//
//        // 设置转码器配置
//        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_EXECUTE_ONLOAD, Boolean.FALSE);
//        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, "http://www.w3.org/2000/svg");
//        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_DOCUMENT_ELEMENT, "svg");
//
//        // 设置输入
//        try (FileInputStream inputStream = new FileInputStream(svgInput)) {
//            TranscoderInput input = new TranscoderInput(inputStream);
//
//            // 设置文件的URI
//            File inputFile = new File(svgInput);
//            input.setURI(inputFile.toURI().toString());
//
//            // 设置输出
//            try (FileOutputStream outputStream = new FileOutputStream(pngOutput)) {
//                TranscoderOutput output = new TranscoderOutput(outputStream);
//
//                try {
//                    // 执行转换
//                    transcoder.transcode(input, output);
//                    log.info("SVG转换成功完成");
//                } catch (Exception e) {
//                    // 记录错误但不抛出，因为即使有CSS错误文件也能正常生成
//                    log.warn("SVG转换过程中出现警告（不影响输出）: {}", e.getMessage());
//                }
//            }
//        }
    }
}
