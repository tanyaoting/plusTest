package com.sylphy.console;

import com.sylphy.config.GeneratorConfig;

import java.io.IOException;

/**
 * 配置加载契约，便于控制台流程在测试中替换配置来源。
 *
 * @author apple
 */
@FunctionalInterface
public interface ConfigLoader {
    GeneratorConfig load() throws IOException;
}
