package dlm.services

import dlm.entities.Fixture
import ele.entities.Point
import extensions.readByte
import extensions.readInt
import extensions.readShort
import services.ParamsParserService
import java.nio.ByteBuffer
import kotlin.experimental.or

class FixtureService : ParamsParserService<Fixture, Fixture> {
    override fun parse(raw: ByteBuffer, params: Fixture): Fixture {
        params.fixtureId = raw.readInt()
        params.offset = Point(raw.readShort(), raw.readShort())
        params.rotation = raw.readShort()
        params.xScale = raw.readShort()
        params.yScale = raw.readShort()
        params.redMultiplier = raw.readByte()
        params.greenMultiplier = raw.readByte()
        params.blueMultiplier = raw.readByte()
        params.hue = params.redMultiplier or params.greenMultiplier or params.blueMultiplier
        params.alpha = raw.readByte()
        return params
    }
}