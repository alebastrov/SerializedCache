import com.nikondsl.cache.CompactingException;
import com.nikondsl.cache.ConcurrentCacheImpl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class ConcurrentCacheImplTest {

    public static void main(String[] args) throws CompactingException {
        ConcurrentCacheImpl cache = new ConcurrentCacheImpl();
        Holder holder1 = new Holder();
        holder1.setVeryBigText("Nikon DSL jhzkjhdskfjhsdzkfjhSDfkjhSDkfjhSKDJfhkjdzxvhhdb=");
        holder1.setSomeNumber(11);
        holder1.setUsual("Igor1");
        holder1.setSomeBytes("new updated value for byte array".getBytes(StandardCharsets.UTF_8));

        Holder holder2 = new Holder();
        holder2.setVeryBigText("XPorter");
        holder2.setSomeNumber(22);
        holder2.setUsual("Igor2");
        holder2.setSomeBytes("byte array to check".getBytes(StandardCharsets.UTF_8));
        Holder.InnerHolder innerHolder = new Holder.InnerHolder();
        innerHolder.setId(1244234);
        innerHolder.setExpression("[\"{\"audience\": [\"call_agent\",\"customer\",\"service_technician\",\"shared_support_customer\"],\"entitlement\": [\"high_value\",\"none\"],\"includeintriage\": [\"no\",\"yes\"],\"outputsensitivecontent\": [\"yes\"],\"troubleshootertype\": [\"dynamic\",\"static\"],\"FHPIEXPANDEDOS\": [\"X100615139615230242710101494121383010312112214\",\"X103811821112152454797835665159522159853\",\"X10428189428422015139408947783598\",\"X1131119239121555440014119964155146012095102\",\"X1133805148910713154448115861498141471210321\",\"X11411761169001501246910878065123542228211\",\"X11501414158842127418081208157841297154315\",\"X11602613510714854103141045582325131113912014\",\"X13134364124155121041411794153914234151221413125\",\"X1381311114014154474110211765351001569151511121\",\"X1407131505131013130415501139799111116911148111\",\"X142821314102613688613179270245905\",\"X1525537611131113410868913117312101412461310213\",\"X18104133713511312460510011121141212671111121149\",\"X18555001531612440131110071171304151915126114\",\"X187406609769427463353535109719481\",\"X19155151528126138943123978111171514719868115\",\"X1931315141181369784151238391511122107122103730\",\"X266795726108369988557377477908555\",\"X307036414225834092304629667468554\",\"X368382202104946806172742827823841\",\"X403914137152714905952038054489414\",\"X465047080665549616181397993848788\",\"X516748388042148194349904326659959\",\"X520481334264406333771701738398407\",\"X538089412883426038147045534640455\",\"X557697776898612740158934428366268\",\"X64208434435522731012429519110534\",\"X704695241187049133437130746958100\",\"X711145833975643682618060423314258\",\"X743848899547851459494534467997058\",\"X805099870624865496205260697974868\",\"X811638673439187539659883670888463\",\"X879973429486841948861718811404087\",\"X184651403277425843090532942361795\",\"X820677652736771455015295675946251\",\"X1710118252340944410118386990658121010953\",\"X1267015021112815401069512287804913141311015\"],\"FHPIMODELS\": [\"X10049307\",\"X10049310\",\"X10049311\",\"X10049316\",\"X10049319\",\"X10049320\",\"X10135644\",\"X10135646\",\"X10135647\",\"X10135652\",\"X10135654\",\"X10135655\",\"X10148200\",\"X10148203\",\"X10148204\",\"X10180322\",\"X10193085\",\"X10193087\",\"X10193150\",\"X10193242\",\"X10193244\",\"X10193061\",\"X10193064\",\"X10193066\",\"X10193247\",\"X10193249\",\"X10193250\",\"X10273051\",\"X10273053\",\"X10273054\",\"X10273066\",\"X10273069\",\"X10273070\",\"X10402766\",\"X10402769\",\"X10402770\",\"X10477245\",\"X10477246\",\"X10477247\",\"X10477250\",\"X10477251\",\"X10477252\",\"X15304490\",\"X10477360\",\"X10477361\",\"X10477362\",\"X16208937\",\"X10477365\",\"X10477366\",\"X10477367\",\"X10477370\",\"X10477371\",\"X10477372\",\"X10522152\",\"X10522155\",\"X10522156\",\"X34748991\",\"X34748992\",\"X34748993\",\"X34748994\",\"X34748995\",\"X34748996\",\"X34748997\",\"X34748998\",\"X34748999\",\"X34749000\",\"X10522163\",\"X10522166\",\"X10522167\",\"X2100019056\",\"X38013691\",\"X38013693\",\"X10669606\",\"X10669609\",\"X10669610\",\"X10669611\",\"X10669613\",\"X10673919\",\"X10669601\",\"X10669604\",\"X10669605\",\"X14142476\",\"X10799989\",\"X10799992\",\"X10799993\",\"X11955052\",\"X10912420\",\"X10912424\",\"X2100019055\",\"X38065545\",\"X38065546\",\"X38065547\",\"X11084767\",\"X11084769\",\"X11084770\",\"X16647017\",\"X11084776\",\"X11084778\",\"X11084780\",\"X16647021\",\"X11122165\",\"X11122167\",\"X11122168\",\"X16647025\",\"X11122282\",\"X11122284\",\"X11122285\",\"X11122292\",\"X11122295\",\"X11122296\",\"X11122334\",\"X11122338\",\"X11122339\",\"X16208849\",\"X9822169\",\"X9822173\",\"X11572363\",\"X11572366\",\"X11572368\",\"X11590564\",\"X11590568\",\"X13201513\",\"X11623703\",\"X11623706\",\"X11623707\",\"X11623683\",\"X11623687\",\"X11623689\",\"X15959069\",\"X2100216744\",\"X11623565\",\"X11623566\",\"X11623691\",\"X11623568\",\"X11623697\",\"X11623701\",\"X15959075\",\"X11623739\",\"X11623742\",\"X11623743\",\"X19005642\",\"X11623749\",\"X11623753\",\"X11623754\",\"X11747118\",\"X11747121\",\"X11747122\",\"X11875183\",\"X11875241\",\"X11875242\",\"X12716695\",\"X12716699\",\"X13555875\",\"X14142470\",\"X37935931\",\"X37935933\",\"X12716702\",\"X12716704\",\"X15475709\",\"X16180456\",\"X12716709\",\"X12716712\",\"X13555877\",\"X15959083\",\"X15959088\",\"X12739376\",\"X12740301\",\"X12739378\",\"X12740303\",\"X12739381\",\"X12740306\",\"X14135013\",\"X13218031\",\"X13218034\",\"X15747777\",\"X2100923817\",\"X2100923818\",\"X13414396\",\"X13414397\",\"X15497436\",\"X13556324\",\"X13556328\",\"X34748945\",\"X34748946\",\"X34748947\",\"X34748948\",\"X34748949\",\"X34748950\",\"X34748951\",\"X34748952\",\"X34748953\",\"X34748954\",\"X34748955\",\"X34748956\",\"X34748957\",\"X34748958\",\"X34748959\",\"X34748960\",\"X34748961\",\"X13624640\",\"X13624644\",\"X13624646\",\"X2100019080\",\"X34748850\",\"X34748852\",\"X34748853\",\"X14169373\",\"X14169376\",\"X15497556\",\"X16269311\",\"X14169439\",\"X14169441\",\"X14309028\",\"X14309030\",\"X15234599\",\"X14432800\",\"X14432804\",\"X14432856\",\"X15959058\",\"X16269312\",\"X2100930209\",\"X14523296\",\"X14523300\",\"X14523303\",\"X17699110\",\"X14627354\",\"X14785515\",\"X14785517\",\"X14840010\",\"X14840013\",\"X15257515\",\"X17699105\",\"X14840019\",\"X14840023\",\"X15257517\",\"X17699101\",\"X2100929879\",\"X14840027\",\"X14840031\",\"X15257520\",\"X17699109\",\"X15234558\",\"X15234603\",\"X15234608\",\"X16180452\",\"X16647033\",\"X15257611\",\"X15257615\",\"X16230530\",\"X17699155\",\"X2100220280\",\"X15257619\",\"X15257622\",\"X15257625\",\"X16647037\",\"X15257632\",\"X15257635\",\"X15257638\",\"X16647041\",\"X15257643\",\"X15257646\",\"X15257647\",\"X16647059\",\"X15257652\",\"X15257655\",\"X15257656\",\"X16180449\",\"X38013514\",\"X38013518\",\"X15287511\",\"X15287512\",\"X15292286\",\"X16647046\",\"X15287507\",\"X15292278\",\"X15292281\",\"X16647067\",\"X15292288\",\"X15292291\",\"X15292292\",\"X2100220500\",\"X15292381\",\"X15292384\",\"X15292385\",\"X16180446\",\"X15292391\",\"X15292394\",\"X15292395\",\"X15292400\",\"X15292403\",\"X15292404\",\"X15292409\",\"X15292411\",\"X15292412\",\"X15292340\",\"X15292341\",\"X15292417\",\"X16425649\",\"X18995735\",\"X15292421\",\"X15292422\",\"X15292452\",\"X16425651\",\"X18995624\",\"X15292427\",\"X15292429\",\"X15292430\",\"X2100927332\",\"X15292440\",\"X15292442\",\"X15292444\",\"X2100927359\",\"X15292434\",\"X15292435\",\"X15292463\",\"X2100219520\",\"X15326589\",\"X15435154\",\"X15435158\",\"X15435161\",\"X17688442\",\"X15441159\",\"X15441161\",\"X15497278\",\"X15497282\",\"X15497285\",\"X16646786\",\"X15497124\",\"X15497126\",\"X15497297\",\"X38013520\",\"X15747876\",\"X15747879\",\"X15747881\",\"X2100655006\",\"X15776523\",\"X15776526\",\"X15776528\",\"X38013516\",\"X38013522\",\"X15831522\",\"X15832459\",\"X15932764\",\"X15932850\",\"X15932853\",\"X15932769\",\"X15932855\",\"X15932858\",\"X15932862\",\"X15932864\",\"X15932866\",\"X2100219859\",\"X15932872\",\"X15932875\",\"X15932877\",\"X15959105\",\"X15973642\",\"X15973645\",\"X15973647\",\"X16110357\",\"X16110360\",\"X16110362\",\"X38023976\",\"X16351110\",\"X16351114\",\"X16351117\",\"X16425523\",\"X16425525\",\"X16425527\",\"X19005637\",\"X16449805\",\"X16449906\",\"X2100614161\",\"X2100614162\",\"X34534927\",\"X34534929\",\"X34534931\",\"X34534933\",\"X34534935\",\"X34534937\",\"X34534939\",\"X34534941\",\"X16449891\",\"X16449894\",\"X2100614078\",\"X2100614085\",\"X2100614086\",\"X21138394\",\"X38461733\",\"X38461735\",\"X38461737\",\"X38461739\",\"X38461741\",\"X38461743\",\"X38461745\",\"X16449796\",\"X16449902\",\"X21085790\",\"X34534913\",\"X34534915\",\"X34534917\",\"X34534919\",\"X34534921\",\"X34534923\",\"X16569561\",\"X16571082\",\"X16612051\",\"X2100018032\",\"X2100018033\",\"X38065558\",\"X38065559\",\"X16665153\",\"X16665155\",\"X16665157\",\"X16748240\",\"X16748245\",\"X16748247\",\"X19515459\",\"X19515469\",\"X19515472\",\"X2100019058\",\"X37935784\",\"X37935786\",\"X37935788\",\"X37935790\",\"X16917750\",\"X16917752\",\"X2100219868\",\"X16917702\",\"X16917705\",\"X17032734\",\"X18269538\",\"X20092244\",\"X17034012\",\"X17034147\",\"X17034019\",\"X17034022\",\"X17034123\",\"X20283138\",\"X32885582\",\"X32885586\",\"X17034129\",\"X20283153\",\"X17047127\",\"X17047130\",\"X19005648\",\"X17047135\",\"X17047138\",\"X19005651\",\"X37936028\",\"X37936030\",\"X37936032\",\"X37936034\",\"X37936036\",\"X37936038\",\"X37936040\",\"X37936042\",\"X37936044\",\"X37936046\",\"X37936048\",\"X37936050\",\"X37936052\",\"X37936054\",\"X37936056\",\"X37936058\",\"X37936060\",\"X37936062\",\"X37936064\",\"X37936066\",\"X37936068\",\"X37936070\",\"X37936072\",\"X37936074\",\"X37936076\",\"X37936078\",\"X37936080\",\"X37936082\",\"X37936084\",\"X37936086\",\"X37936088\",\"X17047144\",\"X17047148\",\"X18995650\",\"X17047152\",\"X17047155\",\"X17047162\",\"X17047165\",\"X17106119\",\"X17106121\",\"X17229505\",\"X23918426\",\"X17571953\",\"X17571955\",\"X17571951\",\"X17571957\",\"X17571949\",\"X17571959\",\"X17571944\",\"X17572031\",\"X17626183\",\"X17638217\",\"X17730990\",\"X17730992\",\"X17958948\",\"X29377722\",\"X17959031\",\"X29377727\",\"X17984508\",\"X17996596\",\"X18269281\",\"X18477185\",\"X18477188\",\"X2100216730\",\"X21502544\",\"X18491272\",\"X18491275\",\"X2100017543\",\"X2100215239\",\"X2100227884\",\"X21502551\",\"X18491277\",\"X18491280\",\"X2100017838\",\"X2100215256\",\"X2100227970\",\"X2100228019\",\"X21502556\",\"X18602169\",\"X18602172\",\"X23565497\",\"X37936177\",\"X37936179\",\"X37936181\",\"X37936183\",\"X37936185\",\"X37936187\",\"X37936189\",\"X37936191\",\"X37936193\",\"X37936195\",\"X37936197\",\"X37936199\",\"X37936201\",\"X37936203\",\"X37936205\",\"X37936207\",\"X37936209\",\"X37936211\",\"X37936213\",\"X37936215\",\"X37936217\",\"X37936219\",\"X37936221\",\"X37936223\",\"X38023712\",\"X38023713\",\"X38023714\",\"X18602177\",\"X18602180\",\"X2100036445\",\"X2100216743\",\"X18640070\",\"X18640073\",\"X2100228077\",\"X2100228247\",\"X23565500\",\"X38352366\",\"X38352367\",\"X38352368\",\"X18809660\",\"X18809661\",\"X18819803\",\"X18819819\",\"X2100216769\",\"X18865577\",\"X18865579\",\"X2100221402\",\"X21085787\",\"X18865628\",\"X18865639\",\"X2100221403\",\"X18865581\",\"X18865646\",\"X2100215085\",\"X18865649\",\"X18865650\",\"X2100228390\",\"X38013944\",\"X38013946\",\"X38013948\",\"X38013950\",\"X38013952\",\"X38013954\",\"X38013956\",\"X38013958\",\"X38013960\",\"X38013962\",\"X38013964\",\"X38013966\",\"X38013968\",\"X38013970\",\"X38013972\",\"X38013974\",\"X38013976\",\"X38013978\",\"X38013980\",\"X38013982\",\"X38013984\",\"X38013986\",\"X18865587\",\"X18865655\",\"X2100018906\",\"X2100018907\",\"X2100018908\",\"X2100018909\",\"X2100018940\",\"X2100018942\",\"X38014026\",\"X38014028\",\"X38014030\",\"X38014032\",\"X38014034\",\"X38014036\",\"X38014038\",\"X38014040\",\"X38014042\",\"X38014044\",\"X38014046\",\"X38014048\",\"X38014050\",\"X38014052\",\"X38014054\",\"X38014056\",\"X38014058\",\"X38014060\",\"X38014062\",\"X38014064\",\"X38014066\",\"X38014068\",\"X38014070\",\"X19404467\",\"X2100923796\",\"X2100923797\",\"X19459955\",\"X19503862\",\"X19503865\",\"X19503840\",\"X19503843\",\"X19503854\",\"X19503856\",\"X19503889\",\"X19503892\",\"X19753493\",\"X19753496\",\"X37936003\",\"X37936004\",\"X37936005\",\"X37936006\",\"X37936007\",\"X37936008\",\"X37936009\",\"X37936010\",\"X37936011\",\"X37936012\",\"X37936013\",\"X19753391\",\"X19753394\",\"X2100016919\",\"X2100625623\",\"X19753488\",\"X...\"]");
        holder2.setInners(Collections.singletonList(innerHolder));


        cache.put("a", holder1);
        cache.put("b", holder2);
        System.err.println(cache.get("a"));
        System.err.println(cache.get("b"));
    }


}