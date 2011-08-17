print('PLANET7 Hello from javascript - message script \r\n') ;
print('PLANET7 Message received ' + stateString[0] + '\r\n');

var v = new java.lang.Runnable()
{
run: function()
  {
  print('PLANET7 - Hello javascript world - from script   **** \r\n') ;
  MyClass.getInitialPosition();
  var0 = stateFloat[0];
  var1 = stateFloat[1];
  var2 = stateFloat[2];
  var3 = var0 + Math.cos(0) * 10;
  var4 = var2 + Math.sin(0) * 10;
  MyClass.setTranslation(var3, var1, var4);
  stateInt[0] = 1;
  while(stateInt[0] == 1)
    {
    myAni();
    MyClass.mySleep(50);
    }

  function myAni()
    {
    now = new Date();
    incr = (now.getSeconds() + (now.getMilliseconds() / 1000)) / 10;
    var3 = var0 + Math.cos(incr * 2 * Math.PI) * 10;
    var4 = var2 + Math.sin(incr * 2 * Math.PI) * 10;

    moonIncr = (now.getSeconds() + (now.getMilliseconds() / 1000)) / 2;
    var5 = var3 + Math.cos(moonIncr * 2 * Math.PI) * 1.0;
    var6 = var4 + Math.sin(moonIncr * 2 * Math.PI) * 1.0;

    MyClass.setTranslation(var5, var1, var6);
    }
  
  }
}

var split_message = stateString[0].split(",");
if(split_message[0] == "101")
    {
    if(split_message[1] == 'start')
        {
        t = new java.lang.Thread(v);
        t.start();
        print('PLANET7 START message for cell \r\n');
        }
    else
        {
        if(split_message[1] == 'stop')
            {
            stateInt[0] = 0;
            MyClass.setTranslation(stateFloat[0], stateFloat[1], stateFloat[2]);
            print('PLANET7 STOP message for cell \r\n');
            }
        else
            {
            print('PLANET7 some other 101 message - ' + stateString[0] + '\r\n');
            }
        }
    }
else
    {
    print('PLANET7 some other message - ' + stateString[0] + '\r\n');
    }

