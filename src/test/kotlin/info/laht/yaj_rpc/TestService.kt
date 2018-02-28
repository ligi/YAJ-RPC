package info.laht.yaj_rpc

import com.google.gson.GsonBuilder
import org.junit.Test

class TestService {

    val gson = GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create()

    @Test
    fun testService() {

        val handler = RpcHandler(SampleService())

        println(handler.getOpenMessage())

        println(gson.fromJson(handler.handle(json1), Map::class.java))
        println(gson.fromJson(handler.handle(json2), Map::class.java))
        println(gson.fromJson(handler.handle(json3), Map::class.java))

    }

    val json1 = """
            {
                "jsonrpc": "2.0",
                "id": 1,
                "method": "SampleService.doubleInput",
                "params": [10]
            }
            """

    val json2 = """
            {
                "jsonrpc": "2.0",
                "id": 1,
                "method": "SampleService.complex",
                "params": [{"i": 1, "d": 2.0, "s": "per"}]
            }
            """

    val json3 = """
            {
                "jsonrpc": "2.0",
                "id": 5,
                "method": "SampleService.returnNothing",
                "params": null
            }
            """
}