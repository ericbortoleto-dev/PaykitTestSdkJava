# Guia de Interoperabilidade Kotlin / Java no SDK Único

Ao integrar o SDK Único em um projeto **Java**, alguns recursos nativos do Kotlin exigem adaptações estruturais e atenção a detalhes de interoperabilidade (como companion objects, parâmetros opcionais e dispatchers de coroutines). 

Abaixo estão levantados os principais pontos de atenção e um exemplo prático de implementação de um método buildPaykit() em Java.

## Pontos de Atenção

1. **Companion Objects**:
   - Métodos declarados dentro de um `companion object` em Kotlin que não possuem a anotação `@JvmStatic` devem ser acessados explicitamente via `.Companion` em Java (ex: `PaykitUiModel.Companion.build(...)`).

2. **Parâmetros Opcionais e Valores Padrão (Default Arguments)**:
   - Funções e construtores em Kotlin que utilizam valores padrão (ex: `useInternalScreens: Boolean = true`, `ioDispatcher = Dispatchers.IO`) não geram automaticamente sobrecargas (*overloads*) em Java a menos que sejam explicitamente anotados com `@JvmOverloads`.
   - Em Java, você precisará passar **todos** os argumentos necessários, preenchendo com `null` ou valores explícitos os parâmetros opcionais que não deseja alterar.

3. **Dispatchers de Coroutines**:
   - Para referenciar dispatchers assíncronos (como o IO) a partir do Java, utilize o getter correspondente fornecido pelo Kotlin, por exemplo: `kotlinx.coroutines.Dispatchers.getIO()`.

---

## Exemplo Prático de PaykitBuilder em Java

Abaixo está um exemplo completo de método utilitário em Java para inicialização e configuração do SDK Único e do modelo de UI:

```java
import androidx.activity.ComponentActivity;
import com.linx.paykit.core.Paykit;
import com.linx.paykit.core.PaykitFactory;
import com.linx.paykit.common.builder.Parameters;
import com.linx.paykit.common.parameter.PaykitId;
import com.linx.paykit.ui.data.PaykitUiModel;

public class PaykitBuilder {

    public static Paykit buildPaykit(ComponentActivity activity) {
        // Métodos de companion object em Kotlin sem @JvmStatic são acessados via .Companion em Java
        // Passando null para callbacks opcionais e explicitando os parâmetros padrão do Kotlin
        PaykitUiModel.Companion.build(
                activity,
                null,                                  // callback opcional
                true,                                  // useInternalScreens
                false,                                 // isDebuggable
                kotlinx.coroutines.Dispatchers.getIO() // dispatcher IO
        );

        return new PaykitFactory().build(
                new Parameters(
                        activity,
                        "AppTeste",
                        new PaykitId("HML-B256A8D9-42B9-45DA-AB63-D31FA5BEF748"), // PaykitId fornecido pela Linx
                        null                                                       // parâmetros opcionais de UI (Parameters.ui)
                )
        );
    }
}
```
